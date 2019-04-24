package com.ihg.soda.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ihg.soda.api.model.BeverageDetail;
import com.ihg.soda.api.model.BeverageRequest;
import com.ihg.soda.api.model.BeverageResponse;
import com.ihg.soda.api.model.ChargeCard;
import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.entity.Beverage;
import com.ihg.soda.api.model.entity.ProductTransaction;
import com.ihg.soda.api.repository.BeverageRepository;
import com.ihg.soda.api.repository.ProductTransactionRepository;
import com.ihg.soda.api.vending.VendingMachine;
import com.ihg.soda.config.ProductConfigurationProperties;
import com.ihg.soda.enums.Denominations;
import com.ihg.soda.enums.LiquidContainerTypes;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.PaymentTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.exception.VendingMachineException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MachineManager {
	
	@Autowired
	private VendingMachine<Beverage, BeverageDetail> sodaMachine;
	@Autowired
	private ProductConfigurationProperties productConfig;
	@Autowired
	private BeverageRepository beverageRepository;
	@Autowired
	private ProductTransactionRepository transactionRepository;

	@PostConstruct
	public void initialize() {
		sodaMachine.setMachineState(MachineStates.AWAIT_PAYMENT);
		Iterable<Beverage> beverageInventory = createBeverageInventory();
		stockVendingMachine(beverageInventory);
	}
	
	/**
	 * <ul>
	 * <li>Build beverages for each brand of beverage (e.g. Coke, Fanta, Sprite)</li>
	 * <li>Persist all beverages - allows for deletion from repository when dispensed</li>
	 * </ul>
	 * @return 
	 * @see {@link ProductBrands}
	 */
	private Iterable<Beverage> createBeverageInventory() {
		List<Beverage> beverageInventory = productConfig.getBeverages().stream()
		.flatMap(beverageDetail -> {
			List<Beverage> beverages = new ArrayList<>();
			IntStream.range(0, sodaMachine.getMaxUnitsPerProduct()).forEach(i -> beverages.add(buildBeverage(beverageDetail)));
			return beverages.stream();
		})
		.collect(Collectors.toList());
		return beverageRepository.saveAll(beverageInventory);
	}

	private Beverage buildBeverage(BeverageDetail beverageDetail) {
		FinancialExchange price = FinancialExchange.builder()
				.amount(BigDecimal.ONE)
				.currency(sodaMachine.getDefaultCurrency())
				.build();
		
		return (Beverage) Beverage.builder()
				.price(price)
				.brand(beverageDetail.getBrand())
				.containerType(beverageDetail.getContainerType())
				.upc(beverageDetail.getUpc())
				.productStatus(ProductStatuses.UNSTOCKED)
				.build();
	}

	/**
	 * Create a {@link Map} where k is {@link BeverageDetail} and
	 * v is an implementation of a {@link Queue} of beverages. 
	 * Also ensures that each beverage queue is limited to the 
	 * maximum number of products specified for said queue.
	 * 
	 * @param beverageInventory
	 */
	private void stockVendingMachine(Iterable<Beverage> beverageInventory) {
		Optional.ofNullable(sodaMachine.getProductStock()).ifPresent(stock -> stock.clear());
		
		EnumSet<ProductStatuses> stockableProductStatuses = EnumSet.of(ProductStatuses.UNSTOCKED, ProductStatuses.STOCKED);
		List<Beverage> beverageList = StreamSupport.stream(beverageInventory.spliterator(), false)
				.filter(bev -> stockableProductStatuses.contains(bev.getProductStatus()))
				.collect(Collectors.toList());
		
		List<Beverage> stockedBeverages = new ArrayList<>();
		Map<BeverageDetail, LinkedList<Beverage>> beverageMap = beverageList.stream()
			.collect(Collectors.groupingBy(bev -> new BeverageDetail(bev.getBrand(), bev.getContainerType()), 
				Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
										.limit(sodaMachine.getMaxUnitsPerProduct().intValue())
										.peek(bev -> {
											bev.setProductStatus(ProductStatuses.STOCKED);
											stockedBeverages.add(bev);
										})
										.collect(Collectors.toCollection(LinkedList::new)))));
		
		sodaMachine.setProductStock(beverageMap);
		beverageRepository.saveAll(stockedBeverages);
	}

	public BeverageResponse processRequest(BeverageRequest request) {
		List<Denominations> cash = request.getCash();
		ChargeCard chargeCard = request.getChargeCard();
		
		PaymentTypes paymentType = PaymentTypes.CASH;
		BigDecimal totalCashInserted = processCashInput(cash);
		handleSelectionMadeWithNoPayment(cash, chargeCard);
		boolean shouldProcessChargeCard = processChargeCardInput(chargeCard);
		
		ProductBrands brand = request.getBrand();
		LiquidContainerTypes containerType = request.getContainerType();
		BeverageDetail productRequested = buildProductRequested(brand, containerType);
		
		LinkedList<Beverage> selectedBeverageQueue = sodaMachine.getProductStock().get(productRequested);
		handleOutOfBeverage(selectedBeverageQueue);
		BigDecimal productPrice = selectedBeverageQueue.stream().findAny().get().getPrice().getAmount();
		
		if(shouldProcessChargeCard) {
			paymentType = PaymentTypes.valueOf(sodaMachine.getCardSwiped());
			if(totalCashInserted.compareTo(BigDecimal.ZERO) == 1) {
				sodaMachine.setMachineState(MachineStates.DISPENSE_CURRENCY);
			}
		} else {
			if(totalCashInserted.compareTo(productPrice) > -1) {
				BigDecimal difference = productPrice.subtract(totalCashInserted).abs();
				sodaMachine.setChangeDue(difference);
				sodaMachine.setMachineState(MachineStates.CHANGE_DUE);
				return buildBeverageResponse(productRequested, selectedBeverageQueue, Optional.of(difference), paymentType);
			} else {
				handleNotEnoughCashTendered(totalCashInserted, productPrice);
			}
		}
		return buildBeverageResponse(productRequested, selectedBeverageQueue, Optional.empty(), paymentType);
	}

	private void handleNotEnoughCashTendered(BigDecimal totalCashInserted, BigDecimal productPrice) {
		sodaMachine.setMachineState(MachineStates.DISPENSE_CURRENCY);
		
		String currencySymbol = sodaMachine.getDefaultCurrency().getSymbol();
		StringBuffer exceptionMessage = new StringBuffer("Price is ")
		.append(currencySymbol)
		.append(productPrice.toString())
		.append(". You gave ")
		.append(currencySymbol)
		.append(totalCashInserted.toString())
		.append(" No drink for you.");
		
		throw new VendingMachineException(exceptionMessage.toString());
	}

	private void handleOutOfBeverage(LinkedList<Beverage> selectedBeverageQueue) {
		if(null == selectedBeverageQueue || selectedBeverageQueue.isEmpty()) {
			sodaMachine.setMachineState(MachineStates.OUT_OF_BEVERAGE);
			throw new VendingMachineException("Sorry, out of your selection");
		}
	}

	private BeverageDetail buildProductRequested(ProductBrands brand, LiquidContainerTypes containerType) {
		if(null == brand || null == containerType) {
			sodaMachine.setMachineState(MachineStates.DISPENSE_CURRENCY);
			sodaMachine.setMachineState(MachineStates.AWAIT_PAYMENT);
			throw new VendingMachineException("Please choose a beverage brand and its packaging");
		}
		
		BeverageDetail productRequested = BeverageDetail.builder()
				.brand(brand)
				.containerType(containerType)
				.build();
		return productRequested;
	}

	private boolean processChargeCardInput(ChargeCard chargeCard) {
		boolean processChargeCard = false;
		if(null != chargeCard) {
			String cardProvider = chargeCard.getProvider().toUpperCase();
			sodaMachine.setCardSwiped(cardProvider);
			sodaMachine.setMachineState(MachineStates.CARD_SWIPED);
			boolean chargeCardSupported = EnumSet.allOf(PaymentTypes.class).stream()
					.map(PaymentTypes::toString)
					.collect(Collectors.toList())
					.contains(cardProvider);
			
			if(chargeCardSupported) {
				processChargeCard = true;
			} else {
				sodaMachine.setMachineState(MachineStates.CARD_READ_ERROR);
				throw new VendingMachineException("Unable to read card");
			}
		}
		return processChargeCard;
	}

	private BigDecimal processCashInput(List<Denominations> cash) {
		BigDecimal cashInserted = BigDecimal.ZERO;
		if(null != cash) {
			cashInserted = cash.stream().map(Denominations::getValue).reduce(cashInserted, BigDecimal::add);
			sodaMachine.setCurrencyInserted(cashInserted);
			sodaMachine.setMachineState(MachineStates.HAS_CURRENCY);
		}
		return cashInserted;
	}

	private void handleSelectionMadeWithNoPayment(List<Denominations> cash, ChargeCard chargeCard) {
		if(null == cash && null == chargeCard) {
			String message = "Please insert at least one currency or swipe charge card to purchase a drink";
			throw new VendingMachineException(message);
		}
	}

	private BeverageResponse buildBeverageResponse(BeverageDetail productDetail, LinkedList<Beverage> beverageQueue, 
			Optional<BigDecimal> calculatedChange, PaymentTypes paymentType) {
		
		Beverage stockedBeverage = beverageQueue.poll();
		sodaMachine.setProductToDispense(stockedBeverage);
		
		productDetail.setUpc(stockedBeverage.getUpc());
		beverageRepository.findById(stockedBeverage.getId()).ifPresent(bev -> {
			bev.setProductStatus(ProductStatuses.SOLD);
			beverageRepository.save(bev);
		});
		
		BigDecimal changeAmount = calculatedChange.orElse(BigDecimal.ZERO);
		
		FinancialExchange paymentTendered = FinancialExchange.builder()
				.amount(changeAmount.add(stockedBeverage.getPrice().getAmount()))
				.currency(sodaMachine.getDefaultCurrency())
				.paymentType(paymentType)
				.build();
		
		ProductTransaction productTransaction = ProductTransaction.builder()
				.beverageDetail(productDetail)
				.changeAmount(changeAmount)
				.product(stockedBeverage)
				.paymentTendered(paymentTendered)
				.build();
		
		ProductTransaction persistedTransaction = transactionRepository.save(productTransaction);
		log.info("Transaction recorded: {}", String.valueOf(persistedTransaction));
		
		BeverageResponse beverageResponse = BeverageResponse.builder()
				.beverageDetail(productDetail)
				.message("Thank you for your business")
				.build();
		
		sodaMachine.setMachineState(MachineStates.PAYMENT_COMPLETE);
		return beverageResponse;
	}

}
