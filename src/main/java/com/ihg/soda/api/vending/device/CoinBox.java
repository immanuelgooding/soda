package com.ihg.soda.api.vending.device;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import com.ihg.soda.api.vending.fund.Coin;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CoinBox extends FundsRepository<Coin> {
	
	public CoinBox(Currency currency) {
		super(currency);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		BigDecimal total = (BigDecimal) evt.getNewValue();
		log.info("Total value of coins deposited: {}{}", getCurrency().getSymbol(),total.setScale(2, RoundingMode.HALF_UP));
	}

	@Override
	protected void displayBoxTotal(BigDecimal total) {
		log.info("Dispensed {}{}", getCurrency().getSymbol(), total.setScale(2, RoundingMode.HALF_UP));
	}
	
}
