package com.ihg.soda.model.service;

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

import com.ihg.soda.config.ProductConfigurationProperties;
import com.ihg.soda.enums.Denominations;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.PaymentTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.model.BeverageDetail;
import com.ihg.soda.model.BeverageRequest;
import com.ihg.soda.model.BeverageResponse;
import com.ihg.soda.model.ChargeCard;
import com.ihg.soda.model.FinancialExchange;
import com.ihg.soda.model.entity.Beverage;
import com.ihg.soda.model.entity.ProductTransaction;
import com.ihg.soda.model.repository.BeverageRepository;
import com.ihg.soda.model.repository.ProductTransactionRepository;
import com.ihg.soda.model.vending.VendingMachine;

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
		List<Beverage> beverageList = StreamSupport.stream(beverageInventory.spliterator(), false)
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
		boolean processChargeCard = false;
		boolean chargeCardSupported = false;

		//begin validations
		List<Denominations> cash = request.getCash();
		BigDecimal cashInserted = BigDecimal.ZERO;
		
		ChargeCard chargeCard = request.getChargeCard();
		
		if(null == cash && null == chargeCard) {
			sodaMachine.setMachineState(MachineStates.AWAIT_PAYMENT);
			return new BeverageResponse();
		}
		
		if(null != cash) {
			cashInserted = cash.stream().map(Denominations::getValue).reduce(cashInserted, BigDecimal::add);
			sodaMachine.setCurrencyInserted(cashInserted);
			sodaMachine.setMachineState(MachineStates.HAS_CURRENCY);
		}
		
		if(null != chargeCard) {
			String cardProvider = chargeCard.getProvider().toUpperCase();
			sodaMachine.setCardSwiped(cardProvider);
			sodaMachine.setMachineState(MachineStates.CARD_SWIPED);
			chargeCardSupported = EnumSet.allOf(PaymentTypes.class).stream()
					.map(PaymentTypes::toString)
					.collect(Collectors.toList())
					.contains(cardProvider);
			
			if(chargeCardSupported) {
				processChargeCard = true;
			} else {
				sodaMachine.setMachineState(MachineStates.CARD_READ_ERROR);
				return new BeverageResponse("Unable to read card");
			}
		}
		// end validations
		
		BeverageDetail productRequested = BeverageDetail.builder()
				.brand(request.getBrand())
				.containerType(request.getContainerType())
				.build();
		
		LinkedList<Beverage> selectedBeverageQueue = sodaMachine.getProductStock().get(productRequested);
		if(null == selectedBeverageQueue || selectedBeverageQueue.isEmpty()) {
			return new BeverageResponse("Sorry, out of your selection");
		}
		
		BigDecimal productPrice = selectedBeverageQueue.stream().findAny().get().getPrice().getAmount();
		
		if(processChargeCard) {
			String cardSwiped = sodaMachine.getCardSwiped();
			return buildBeverageResponse(productRequested, selectedBeverageQueue, Optional.empty(), PaymentTypes.valueOf(cardSwiped ));
		} else {
			if(cashInserted.compareTo(productPrice) > -1) {
				BigDecimal difference = productPrice.subtract(cashInserted).abs();
				sodaMachine.setChangeDue(difference);
				sodaMachine.setMachineState(MachineStates.CHANGE_DUE);
				return buildBeverageResponse(productRequested, selectedBeverageQueue, Optional.of(difference), PaymentTypes.CASH);
			}
		}
		String message = "Price is ".concat(productPrice.toString())
				.concat(". You gave ").concat(cashInserted.toString())
				.concat(" No drink for you.");
		return new BeverageResponse(message);
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
		sodaMachine.setMachineState(MachineStates.AWAIT_PAYMENT);
		
		return beverageResponse;
	}

}
