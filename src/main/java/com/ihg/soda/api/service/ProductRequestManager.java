package com.ihg.soda.api.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.ProductDetail;
import com.ihg.soda.api.model.entity.DispensableProduct;
import com.ihg.soda.api.model.entity.SaleTransaction;
import com.ihg.soda.api.model.request.CardRequest;
import com.ihg.soda.api.model.request.CashRequest;
import com.ihg.soda.api.model.response.ProductResponse;
import com.ihg.soda.api.repository.DispensableProductRepository;
import com.ihg.soda.api.repository.SaleTransactionRepository;
import com.ihg.soda.api.vending.device.VendingMachine;
import com.ihg.soda.api.vending.fund.ChargeCard;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.PaymentTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.exception.ChargeCardException;
import com.ihg.soda.exception.InsufficientFundsException;
import com.ihg.soda.exception.MissingProductDetailException;
import com.ihg.soda.exception.ProductOutOfStockException;
import com.ihg.soda.exception.VendingMachineException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductRequestManager {
	
	@Autowired
	private VendingMachine vendingMachine;
	@Autowired
	private DispensableProductRepository productRepository;
	@Autowired
	private SaleTransactionRepository saleTransactionRepository;

	public ProductResponse handleCashRequest(CashRequest request) {
		BigDecimal coinsTotal = vendingMachine.calculateCoins(request.getCoins());
		BigDecimal bankNotesTotal = vendingMachine.calculateBankNotes(request.getBankNotes());
		try {
			return buildProductResponse(request.getBrand(), request.getPackaging(), coinsTotal.add(bankNotesTotal));
		} catch (MissingProductDetailException | ProductOutOfStockException | InsufficientFundsException e) {
			throw new VendingMachineException(e);
		}
	}
	
	public ProductResponse handleCardRequest(CardRequest request) {
		ChargeCard chargeCard = request.getChargeCard();
		Optional.ofNullable(chargeCard).orElseThrow(() -> new VendingMachineException("Charge card is required"));
		try {
			return buildProductResponse(request.getBrand(), request.getPackaging(), chargeCard);
		} catch (MissingProductDetailException | ProductOutOfStockException | InsufficientFundsException | ChargeCardException e) {
			throw new VendingMachineException(e);
		}
	}

	private ProductResponse buildProductResponse(ProductBrands brand, PackagingTypes packaging, ChargeCard chargeCard) 
		throws MissingProductDetailException, ProductOutOfStockException, InsufficientFundsException, ChargeCardException {
		
		String provider = chargeCard.getProvider().toUpperCase();
		Optional.ofNullable(provider).orElseThrow(() -> new VendingMachineException("Card provider is required"));
		
		PaymentTypes paymentType = Arrays.asList(PaymentTypes.values()).stream().filter(pt -> String.valueOf(pt).equals(provider)).findAny()
		.orElseThrow(() -> new VendingMachineException("Charge card not supported"));

		DispensableProduct stockedProduct = getStockedProduct(brand, packaging);
		boolean cardPaymentAccepted = vendingMachine.cardPaymentAccepted(chargeCard, stockedProduct.getPrice().getAmount());
		if(cardPaymentAccepted) {
			createSaleTransaction(stockedProduct, paymentType, BigDecimal.ZERO);
			return buildProductResponse(stockedProduct.getProductDetail());
		} else {
			throw new ChargeCardException("Payment declined");
		}
	}
	
	private ProductResponse buildProductResponse(ProductBrands brand, PackagingTypes packaging, BigDecimal cashTotal) 
		throws MissingProductDetailException, ProductOutOfStockException, InsufficientFundsException {
		
		DispensableProduct stockedProduct = getStockedProduct(brand, packaging);
		processCashPayment(stockedProduct, cashTotal);
		return buildProductResponse(stockedProduct.getProductDetail());
	}
	
	private DispensableProduct getStockedProduct(ProductBrands brand, PackagingTypes packaging) 
		throws ProductOutOfStockException, MissingProductDetailException {
		
		ProductDetail productDetail = buildProductRequested(brand, packaging, Optional.empty());
		LinkedList<DispensableProduct> productQueue = vendingMachine.getProductStock().get(productDetail);
		handleProductOutOfStock(productQueue);
		
		return productQueue.poll();
	}

	private ProductDetail buildProductRequested(ProductBrands brand, PackagingTypes containerType, Optional<Long> upc) 
			throws MissingProductDetailException {
		
		if(null == brand || null == containerType) {
			throw new MissingProductDetailException("Product brand and packaging are required to make a purchase");
		}
		return ProductDetail.builder()
				.brand(brand)
				.packaging(containerType)
				.upc(upc.orElse(null))
				.build();
	}

	private void handleProductOutOfStock(LinkedList<DispensableProduct> selectedBeverageQueue) 
		throws ProductOutOfStockException {
		
		if(null == selectedBeverageQueue || selectedBeverageQueue.isEmpty()) {
			vendingMachine.setMachineState(MachineStates.OUT_OF_PRODUCT);
			throw new ProductOutOfStockException("Product out of stock");
		}
	}
	
	private void processCashPayment(DispensableProduct stockedProduct, BigDecimal cashTotal)
		throws InsufficientFundsException {
		
		BigDecimal productPrice = stockedProduct.getPrice().getAmount(); //TODO; good time to add promotion
		if(cashTotal.compareTo(productPrice) > -1) {
			BigDecimal difference = productPrice.subtract(cashTotal).abs();
			vendingMachine.setChangeDue(difference);
			vendingMachine.setMachineState(MachineStates.CHANGE_DUE);
			createSaleTransaction(stockedProduct, PaymentTypes.CASH, difference);
		} else {
			handleInsufficientFunds(cashTotal, productPrice);
		}
	}

	private void handleInsufficientFunds(BigDecimal totalCashInserted, BigDecimal productPrice) 
			throws InsufficientFundsException {
		
		vendingMachine.setMachineState(MachineStates.DISPENSE_CASH);
		
		String currencySymbol = vendingMachine.getDefaultCurrency().getSymbol();
		StringBuffer exceptionMessage = new StringBuffer("Price is ")
		.append(currencySymbol)
		.append(productPrice.toString())
		.append(", you gave ")
		.append(currencySymbol)
		.append(totalCashInserted.toString())
		.append(" - please deposit ")
		.append(currencySymbol)
		.append(productPrice.subtract(totalCashInserted))
		.append(" to complete the purchase");
		
		throw new InsufficientFundsException(exceptionMessage.toString());
	}
	
	private void createSaleTransaction(DispensableProduct stockedProduct, PaymentTypes paymentType, 
			BigDecimal changeAmount) {
		
		vendingMachine.setProductToDispense(stockedProduct);
		
		productRepository.findById(stockedProduct.getId()).ifPresent(product -> {
			product.setProductStatus(ProductStatuses.SOLD);
			productRepository.save(product);
		});
		
		FinancialExchange paymentTendered = FinancialExchange.builder()
				.amount(changeAmount.add(stockedProduct.getPrice().getAmount()))
				.currency(vendingMachine.getDefaultCurrency())
				.paymentType(paymentType)
				.build();
		
		SaleTransaction saleTransaction = SaleTransaction.builder()
				.productDetail(stockedProduct.getProductDetail())
				.changeAmount(changeAmount)
				.product(stockedProduct)
				.paymentTendered(paymentTendered)
				.build();
		
		SaleTransaction persistedTransaction = saleTransactionRepository.save(saleTransaction);
		log.info("Transaction recorded: {}", String.valueOf(persistedTransaction));
	}

	private ProductResponse buildProductResponse(ProductDetail productDetail) {
		ProductResponse productResponse = ProductResponse.builder()
				.productDetail(productDetail)
				.message("Thank you for your business")
				.build();
		
		vendingMachine.setMachineState(MachineStates.PAYMENT_COMPLETE);
		return productResponse;
	}

}
