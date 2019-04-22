package com.ihg.soda.model;

import java.math.BigDecimal;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ihg.soda.enums.PaymentTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class FinancialExchange {

	@Column
	private Currency currency;
	@Column
	private BigDecimal amount;
	@Column
	@Enumerated(EnumType.STRING)
	private PaymentTypes paymentType;
	
	@Override
	public String toString() {
		return String.join(currency.getCurrencyCode(), " ", String.valueOf(amount));
	}
	
}
