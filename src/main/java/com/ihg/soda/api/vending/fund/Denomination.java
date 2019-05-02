package com.ihg.soda.api.vending.fund;

import java.math.BigDecimal;

import com.ihg.soda.enums.DenominationTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Denomination {
	
	private BigDecimal amount;
	private String commonName;
	
	public abstract DenominationTypes getDenominationType();
	
}
