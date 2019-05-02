package com.ihg.soda.api.vending.device;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.util.Currency;

import com.ihg.soda.api.vending.fund.BankNote;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class BankNoteBox extends FundsRepository<BankNote> {
	
	public BankNoteBox(Currency currency) {
		super(currency);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		BigDecimal total = (BigDecimal) evt.getNewValue();
		log.info("Total value of bank notes deposited: {}{}", getCurrency().getSymbol(),total.setScale(0));
	}
	
	@Override
	protected void displayBoxTotal(BigDecimal total) {
		log.info("Dispensed {}{}", getCurrency().getSymbol(), total.setScale(0));
	}
	
}
