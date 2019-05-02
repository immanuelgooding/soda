package com.ihg.soda.api.vending.fund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeCard {
	
	private String name;
	private Long cardNumber;
	private String expiryMonth;
	private Short expiryYear;
	private String provider;

}