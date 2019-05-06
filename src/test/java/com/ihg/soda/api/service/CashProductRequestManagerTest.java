package com.ihg.soda.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.ProductDetail;
import com.ihg.soda.api.model.entity.DispensableProduct;
import com.ihg.soda.api.model.entity.SaleTransaction;
import com.ihg.soda.api.model.request.CashRequest;
import com.ihg.soda.api.repository.DispensableProductRepository;
import com.ihg.soda.api.repository.SaleTransactionRepository;
import com.ihg.soda.api.vending.device.VendingMachine;
import com.ihg.soda.api.vending.fund.BankNote;
import com.ihg.soda.api.vending.fund.Coin;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.PaymentTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.exception.VendingMachineException;


public class CashProductRequestManagerTest {

	@Mock
	private VendingMachine vendingMachine;
	@Mock
	private DispensableProductRepository productRepository;
	@Mock
	private SaleTransactionRepository transactionRepository;
	@Mock
	private HashMap<ProductDetail, LinkedList<DispensableProduct>> mockProductStock = new HashMap<>();
	
	
	@Captor
	private ArgumentCaptor<BigDecimal> transactionChangeArgument;
	@Captor
	private ArgumentCaptor<DispensableProduct> productToDispenseArgument;
	@Captor
	private ArgumentCaptor<DispensableProduct> productRepositoryArgument;
	
	
	@InjectMocks
	private ProductRequestManager requestManager;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testHandleCashRequest_success_noChange_expected() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(BigDecimal.ONE);
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.ZERO);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		
		DispensableProduct dispensableProduct = generateDispensableProduct(PaymentTypes.CASH);
		Collection<DispensableProduct> collection = Collections.singleton(dispensableProduct);
		Currency productPriceCurrency = dispensableProduct.getPrice().getCurrency();
		
		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(new LinkedList<DispensableProduct>(collection));
		doNothing().when(vendingMachine).setProductToDispense(any(DispensableProduct.class));
		when(vendingMachine.getDefaultCurrency()).thenReturn(productPriceCurrency, productPriceCurrency);
		when(productRepository.findById(anyLong())).thenReturn(Optional.of(dispensableProduct));
		when(transactionRepository.save(any(SaleTransaction.class))).thenReturn(SaleTransaction.builder().build());
	
		requestManager.handleCashRequest(generateCashRequest());
		
		verify(vendingMachine).calculateCoins(ArgumentMatchers.<List<Coin>>any());
		verify(vendingMachine).calculateBankNotes(ArgumentMatchers.<List<BankNote>>any());
		verify(vendingMachine).getProductStock();
		
		verify(vendingMachine, never()).setChangeDue(transactionChangeArgument.capture());
		verify(vendingMachine, never()).setMachineState(eq(MachineStates.CHANGE_DUE));
		
		verify(vendingMachine).setProductToDispense(productToDispenseArgument.capture());
		assertEquals(productToDispenseArgument.getValue(), dispensableProduct);
		
		verify(productRepository).findById(anyLong());
		verify(productRepository).save(productRepositoryArgument.capture());
		assertEquals(productRepositoryArgument.getValue().getPrice(), 
				productToDispenseArgument.getValue().getPrice());
		assertEquals(productRepositoryArgument.getValue().getProductStatus(), ProductStatuses.SOLD);
		
		verify(transactionRepository).save(any(SaleTransaction.class));
		verify(vendingMachine).setMachineState(eq(MachineStates.PAYMENT_COMPLETE));
	}
	
	@Test
	public void testHandleCashRequest_success_change_expected() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(BigDecimal.ONE);
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.TEN);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		
		DispensableProduct dispensableProduct = generateDispensableProduct(PaymentTypes.CASH);
		Collection<DispensableProduct> collection = Collections.singleton(dispensableProduct);
		Currency productPriceCurrency = dispensableProduct.getPrice().getCurrency();

		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(new LinkedList<DispensableProduct>(collection));
		doNothing().when(vendingMachine).setProductToDispense(any(DispensableProduct.class));
		when(vendingMachine.getDefaultCurrency()).thenReturn(productPriceCurrency, productPriceCurrency);
		when(productRepository.findById(anyLong())).thenReturn(Optional.of(dispensableProduct));
		when(transactionRepository.save(any(SaleTransaction.class))).thenReturn(SaleTransaction.builder().build());
	
		requestManager.handleCashRequest(generateCashRequest());
		
		verify(vendingMachine).calculateCoins(ArgumentMatchers.<List<Coin>>any());
		verify(vendingMachine).calculateBankNotes(ArgumentMatchers.<List<BankNote>>any());
		verify(vendingMachine).getProductStock();
		
		verify(vendingMachine).setChangeDue(transactionChangeArgument.capture());
		assertEquals(transactionChangeArgument.getValue().compareTo(BigDecimal.ZERO), 1);
		
		verify(vendingMachine).setMachineState(eq(MachineStates.CHANGE_DUE));
		
		verify(vendingMachine).setProductToDispense(productToDispenseArgument.capture());
		assertEquals(productToDispenseArgument.getValue(), dispensableProduct);
		
		verify(productRepository).findById(anyLong());
		verify(productRepository).save(productRepositoryArgument.capture());
		assertEquals(productRepositoryArgument.getValue().getPrice(), 
				productToDispenseArgument.getValue().getPrice());
		assertEquals(productRepositoryArgument.getValue().getProductStatus(), ProductStatuses.SOLD);
		
		verify(transactionRepository).save(any(SaleTransaction.class));
		verify(vendingMachine).setMachineState(eq(MachineStates.PAYMENT_COMPLETE));
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCashRequest_insufficient_funds_shouldThrowException() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(new BigDecimal(0.5d));
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.ZERO);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		
		DispensableProduct dispensableProduct = generateDispensableProduct(PaymentTypes.CASH);
		Collection<DispensableProduct> collection = Collections.singleton(dispensableProduct);
		Currency productPriceCurrency = dispensableProduct.getPrice().getCurrency();

		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(new LinkedList<DispensableProduct>(collection));
		doNothing().when(vendingMachine).setProductToDispense(any(DispensableProduct.class));
		when(vendingMachine.getDefaultCurrency()).thenReturn(productPriceCurrency);
	
		requestManager.handleCashRequest(generateCashRequest());
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCashRequest_null_productQueue_shouldThrowException() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(new BigDecimal(0.5d));
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.ZERO);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(null);
		
		requestManager.handleCashRequest(generateCashRequest());
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCashRequest_empty_productQueue_shouldThrowException() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(new BigDecimal(0.5d));
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.ZERO);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(new LinkedList<DispensableProduct>());
		
		requestManager.handleCashRequest(generateCashRequest());
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCashRequest_nullBrand_shouldThrowException() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(new BigDecimal(0.5d));
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.ZERO);
		
		CashRequest cashRequest = generateCashRequest();
		cashRequest.setBrand(null);
		requestManager.handleCashRequest(cashRequest);
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCashRequest_nullPackaging_shouldThrowException() {
		when(vendingMachine.calculateCoins(ArgumentMatchers.<List<Coin>>any())).thenReturn(new BigDecimal(0.5d));
		when(vendingMachine.calculateBankNotes(ArgumentMatchers.<List<BankNote>>any())).thenReturn(BigDecimal.ZERO);
		
		CashRequest cashRequest = generateCashRequest();
		cashRequest.setPackaging(null);
		requestManager.handleCashRequest(cashRequest);
	}
	
	private DispensableProduct generateDispensableProduct(PaymentTypes paymentType) {
		FinancialExchange price = FinancialExchange.builder()
				.amount(BigDecimal.ONE)
				.currency(Currency.getInstance("USD"))
				.paymentType(paymentType)
				.build();
		
		return DispensableProduct.builder()
				.id(Long.MIN_VALUE)
				.price(price)
				.build();
	}
	
	private CashRequest generateCashRequest() {
		return generateCashRequest(null, null);
	}
	
	private CashRequest generateCashRequest(List<Coin> coins, List<BankNote> bankNotes) {
		return CashRequest.builder()
				.brand(ProductBrands.SEVEN_UP)
				.packaging(PackagingTypes.BOX)
				.coins(coins)
				.bankNotes(bankNotes)
				.build();
	}
	
}
