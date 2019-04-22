package com.ihg.soda.model.vending;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedList;
import java.util.Map;

import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.model.entity.DispensableProduct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public abstract class VendingMachine<T extends DispensableProduct, U>
	implements PropertyChangeListener {

	private Short numberOfProductQueues;
	private Short maxUnitsPerProduct;
	private Currency defaultCurrency;
	private Map<U, LinkedList<T>> productStock;
	
	private BigDecimal currencyInserted;
	private BigDecimal changeDue;
	private String cardSwiped;
	private T productToDispense;
	@Getter
	private MachineStates machineState;
	
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	public VendingMachine() {
		addPropertyChangeListener(this);
	}
	
	public abstract boolean providesChilledProducts();
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	public void setMachineState(MachineStates machineState) {
		support.firePropertyChange("machineState", this.machineState, machineState);
		this.machineState = machineState;
	}
	
	public final void dispenseCurrency() {
		log.info("Dispensed {}{}", defaultCurrency.getSymbol(), currencyInserted);
		currencyInserted = null;
	}
	
	public final void dispenseChange() {
		log.info("Dispensed {}{}", defaultCurrency.getSymbol(), changeDue);
		changeDue = null;
	}
	
	public final void dispenseProduct() {
		log.info("Dispensed {}", productToDispense.getBrand());
		productToDispense = null;
		changeDue = null;
		currencyInserted = null;
		cardSwiped = null;
	}
	
	public final void displayWelcomeMessage() {
		log.info("Welcome");
	}
	
	public final void displayCardReadErrorMessage() {
		log.info("Card Read Error");
	}
	
	public final void displaySelectProductMessage() {
		log.info("Select Your Product");
	}
	
	public final void displayCurrencyInserted() {
		log.info("Currency Inserted: {}{}", defaultCurrency.getSymbol(), currencyInserted);
	}
	
	public final void displayCardInfo() {
		log.info("Card Swiped: {}", cardSwiped);
	}
	
}
