package com.ihg.soda.model.vending;

import java.util.Currency;
import java.util.LinkedList;
import java.util.Map;

import com.ihg.soda.model.entity.DispensableProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class DispensingMachineOther<T extends DispensableProduct, U> {

	private Short numberOfProductQueues;
	private Short maxUnitsPerProduct;
	private Currency defaultCurrency;
	private Map<U, LinkedList<T>> productStock;
//	private Set<Denominations> acceptedDenominations;
//	private Set<MachineStates> machineState;
//	
//	private final Predicate<Denominations> currencyFilter = denomination -> acceptedDenominations.contains(denomination);
//			
//	public abstract boolean providesChilledProducts();
//	
//	public final List<Denominations> rejectedDenominations(Denominations... denominations) {
//		return Stream.of(denominations)
//				.filter(currencyFilter.negate())
//				.peek(denomination -> log.info("Rejected tendered denomination {}", denomination.getPaymentExchange()))
//				//could publish reject paymentForm event here (e.g. dispense tendered payment)
//				.collect(Collectors.toList());
//	}
//	
//	public final Denominations buildDenomination(Double amount, PaymentTypes paymentType) {
//		FinancialExchange paymentExchange = FinancialExchange.builder()
//				.amount(new BigDecimal(amount))
//				.currency(defaultCurrency)
//				.build();
//		
//		return Denominations.builder()
//				.paymentExchange(paymentExchange)
//				.paymentType(paymentType)
//				.build();
//	}
	
}
