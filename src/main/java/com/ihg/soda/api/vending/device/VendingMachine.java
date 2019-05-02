package com.ihg.soda.api.vending.device;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.ihg.soda.api.model.ProductDetail;
import com.ihg.soda.api.model.entity.DispensableProduct;
import com.ihg.soda.api.vending.fund.BankNote;
import com.ihg.soda.api.vending.fund.ChargeCard;
import com.ihg.soda.api.vending.fund.Coin;
import com.ihg.soda.enums.MachineStates;
import com.ihg.soda.exception.VendingMachineException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class VendingMachine implements PropertyChangeListener {

	@Setter(value = AccessLevel.NONE)
	private Currency defaultCurrency = Currency.getInstance(Locale.getDefault());
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private FundsRepository<Coin> coinBox;
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private FundsRepository<BankNote> bankNoteBox;
	@Setter(value = AccessLevel.NONE)
	@Getter(value = AccessLevel.NONE)
	private ChargeCardReader cardReader;

	private Short numberOfProductQueues;
	private Short maxUnitsPerProduct;
	private Map<ProductDetail, LinkedList<DispensableProduct>> productStock;
	
	private final PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	private BigDecimal changeDue;
	private DispensableProduct productToDispense;
	@Getter
	private MachineStates machineState;
	
	private VendingMachine() {
		cardReader = new ChargeCardReader();
		addPropertyChangeListener(this);
	}
	
	public VendingMachine(Currency defaultCurrency) {
		this();
		Optional.ofNullable(defaultCurrency).ifPresent(currency -> this.defaultCurrency = currency);
		try {
			coinBox = new CoinBox(this.defaultCurrency);
			bankNoteBox = new BankNoteBox(this.defaultCurrency);
		} catch (IllegalArgumentException e) {
			throw new VendingMachineException(e);
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		MachineStates latestMachineState = MachineStates.valueOf(evt.getNewValue().toString());
		switch(latestMachineState) {
		case AWAIT_PAYMENT:
			displayWelcomeMessage();
			break;
		case DISPENSE_PRODUCT:
			dispenseProduct();
			setMachineState(MachineStates.AWAIT_PAYMENT);
			break;
		case CHANGE_DUE:
			dispenseChange();
			break;
		case PAYMENT_COMPLETE:
			setMachineState(MachineStates.DISPENSE_PRODUCT);
			break;
		case OUT_OF_PRODUCT:
			displayOutOfProductMessage();
			setMachineState(MachineStates.DISPENSE_CASH);
			break;
		case DISPENSE_CASH:
			coinBox.dispenseFunds();
			bankNoteBox.dispenseFunds();
			setMachineState(MachineStates.AWAIT_PAYMENT);
			break;
		default:
			displayWelcomeMessage();
		}
	}
	
	public BigDecimal calculateCoins(List<Coin> coins) {
		return coinBox.calculateDenominations(coins);
	}
	
	public BigDecimal calculateBankNotes(List<BankNote> bankNotes) {
		return bankNoteBox.calculateDenominations(bankNotes);
	}

	public boolean cardPaymentAccepted(ChargeCard chargeCard, BigDecimal amount) {
		return cardReader.cardPaymentAccepted(chargeCard, amount);
	}
	
	public void setMachineState(MachineStates machineState) {
		support.firePropertyChange("machineState", this.machineState, machineState);
		this.machineState = machineState;
	}
	
	public final void dispenseChange() {
		if(changeDue.compareTo(BigDecimal.ZERO) == 1) {
			log.info("Dispensed {}{}", defaultCurrency.getSymbol(), changeDue.setScale(2, RoundingMode.HALF_UP));
		}
		changeDue = null;
	}
	
	public final void dispenseProduct() {
		log.info("Dispensed {}", productToDispense.getProductDetail().getBrand());
		productToDispense = null;
		changeDue = null;
	}
	
	public final void displayWelcomeMessage() {
		log.info("Welcome");
	}

	
	public final void displaySelectProductMessage() {
		log.info("Select Your Product");
	}
	
	public final void displayOutOfProductMessage() {
		log.info("Product out of stock");
	}
	
}
