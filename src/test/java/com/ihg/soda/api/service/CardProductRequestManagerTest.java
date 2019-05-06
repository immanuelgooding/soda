package com.ihg.soda.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.ProductDetail;
import com.ihg.soda.api.model.entity.DispensableProduct;
import com.ihg.soda.api.model.entity.SaleTransaction;
import com.ihg.soda.api.model.request.CardRequest;
import com.ihg.soda.api.repository.DispensableProductRepository;
import com.ihg.soda.api.repository.SaleTransactionRepository;
import com.ihg.soda.api.vending.device.VendingMachine;
import com.ihg.soda.api.vending.fund.ChargeCard;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.PaymentTypes;
import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.exception.VendingMachineException;


public class CardProductRequestManagerTest {

	@Mock
	private VendingMachine vendingMachine;
	@Mock
	private DispensableProductRepository productRepository;
	@Mock
	private SaleTransactionRepository transactionRepository;
	@Mock
	private HashMap<ProductDetail, LinkedList<DispensableProduct>> mockProductStock = new HashMap<>();
	
	
	@Captor
	private ArgumentCaptor<ChargeCard> cardArgument;
	@Captor
	private ArgumentCaptor<DispensableProduct> productToDispenseArgument;
	@Captor
	private ArgumentCaptor<DispensableProduct> productRepositoryArgument;
	@Captor
	private ArgumentCaptor<SaleTransaction> saleTransactionRepositoryArgument;
	
	
	@InjectMocks
	private ProductRequestManager requestManager;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testHandleCardRequest_success() {
		CardRequest cardRequest = generateCardRequest();
		String provider = cardRequest.getChargeCard().getProvider();
		
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		
		DispensableProduct dispensableProduct = generateDispensableProduct(PaymentTypes.valueOf(provider));
		Collection<DispensableProduct> collection = Collections.singleton(dispensableProduct);
		Currency productPriceCurrency = dispensableProduct.getPrice().getCurrency();
		
		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(new LinkedList<DispensableProduct>(collection));
		doNothing().when(vendingMachine).setProductToDispense(any(DispensableProduct.class));
		when(vendingMachine.getDefaultCurrency()).thenReturn(productPriceCurrency);
		when(productRepository.findById(anyLong())).thenReturn(Optional.of(dispensableProduct));
		when(transactionRepository.save(any(SaleTransaction.class))).thenReturn(SaleTransaction.builder().build());
	
		requestManager.handleCardRequest(cardRequest);
		
		verify(vendingMachine).cardPaymentAccepted(cardArgument.capture(), any(BigDecimal.class));
		assertEquals(cardArgument.getValue().getProvider(), provider);
		
		verify(vendingMachine).getProductStock();
		verify(vendingMachine).setProductToDispense(productToDispenseArgument.capture());
		assertEquals(productToDispenseArgument.getValue(), dispensableProduct);
		
		
		verify(productRepository).findById(anyLong());
		verify(productRepository).save(productRepositoryArgument.capture());
		assertEquals(productRepositoryArgument.getValue().getPrice(), 
				productToDispenseArgument.getValue().getPrice());
		assertEquals(productRepositoryArgument.getValue().getProductStatus(), ProductStatuses.SOLD);
		
		verify(transactionRepository).save(saleTransactionRepositoryArgument.capture());
		assertTrue(saleTransactionRepositoryArgument.getValue().getChangeAmount().compareTo(BigDecimal.ZERO) == 0);
		
		verify(vendingMachine).setMachineState(eq(MachineStates.PAYMENT_COMPLETE));
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_null_productQueue_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(null);
		
		requestManager.handleCardRequest(generateCardRequest());
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_empty_productQueue_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		when(vendingMachine.getProductStock()).thenReturn(mockProductStock);
		when(mockProductStock.get(any(ProductDetail.class))).thenReturn(new LinkedList<DispensableProduct>());
		
		requestManager.handleCardRequest(generateCardRequest());
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_nullBrand_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		
		CardRequest cardRequest = generateCardRequest();
		cardRequest.setBrand(null);
		requestManager.handleCardRequest(cardRequest);
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_nullPackaging_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		
		CardRequest cardRequest = generateCardRequest();
		cardRequest.setPackaging(null);
		requestManager.handleCardRequest(cardRequest);
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_nullChargeCard_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		
		CardRequest cardRequest = generateCardRequest();
		cardRequest.setChargeCard(null);
		requestManager.handleCardRequest(cardRequest);
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_nullCardProvider_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		requestManager.handleCardRequest(generateCardRequest(null));
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_emptyCardProvider_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
		requestManager.handleCardRequest(generateCardRequest(""));
	}
	
	@Test(expected = VendingMachineException.class)
	public void testHandleCardRequest_cardDeclined_shouldThrowException() {
		when(vendingMachine.cardPaymentAccepted(any(ChargeCard.class), any(BigDecimal.class))).thenReturn(Boolean.FALSE);
		requestManager.handleCardRequest(generateCardRequest());
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
	
	private CardRequest generateCardRequest() {
		return generateCardRequest(String.valueOf(PaymentTypes.MASTERCARD));
	}
	
	private CardRequest generateCardRequest(String provider) {
		ChargeCard chargeCard = ChargeCard.builder().provider(provider).build();
		return CardRequest.builder()
				.brand(ProductBrands.DR_PEPPER)
				.packaging(PackagingTypes.BOTTLE)
				.chargeCard(chargeCard)
				.build();
	}

}
