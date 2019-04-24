package com.ihg.soda.api.vending;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

import com.ihg.soda.api.model.BeverageDetail;
import com.ihg.soda.api.model.entity.Beverage;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.enums.ProductTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SodaMachine extends VendingMachine<Beverage, BeverageDetail> {
	
	private final ProductTypes productType = ProductTypes.DRINK;
	private BeverageDetail beverageDetail;
	
	@Override
	public boolean providesChilledProducts() {
		return true;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		MachineStates latestMachineState = MachineStates.valueOf(evt.getNewValue().toString());
		switch(latestMachineState) {
		case AWAIT_PAYMENT:
			displayWelcomeMessage();
			break;
		case CARD_READ_ERROR:
			displayCardReadErrorMessage();
			break;
		case DISPENSE_CURRENCY:
			dispenseCurrency();
			setMachineState(MachineStates.AWAIT_PAYMENT);
			break;
		case CARD_SWIPED:
			if(null != getCurrencyInserted()) {
				dispenseCurrency();
			}
			displayCardInfo();
			break;
		case DISPENSE_PRODUCT:
			dispenseProduct();
			break;
		case HAS_CURRENCY:
			displayCurrencyInserted();
			break;
		case CHANGE_DUE:
			if(BigDecimal.ZERO.compareTo(getChangeDue()) < 1) {
				dispenseChange();
			}
			break;
		case PAYMENT_COMPLETE:
			setMachineState(MachineStates.DISPENSE_PRODUCT);
			break;
		case OUT_OF_BEVERAGE:
			setMachineState(MachineStates.DISPENSE_CURRENCY);
			break;
		default:
			displayWelcomeMessage();
		}
	}
	
}
