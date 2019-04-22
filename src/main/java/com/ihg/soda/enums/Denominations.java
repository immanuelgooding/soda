
package com.ihg.soda.enums;

import java.math.BigDecimal;

public enum Denominations {
	NICKEL(0.05), DIME(0.10), QUARTER(0.25), BILL_1(1.00);
	
	private double value;
	
	private Denominations(double value) {
		this.value = value;
	}
	
	public BigDecimal getValue() {
		return BigDecimal.valueOf(value);
	}
}
