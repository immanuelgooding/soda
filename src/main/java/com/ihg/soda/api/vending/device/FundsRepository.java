package com.ihg.soda.api.vending.device;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ihg.soda.api.vending.fund.Denomination;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FundsRepository<T extends Denomination> implements PropertyChangeListener {

	private List<T> denominations;
	private BigDecimal fundsTotalValue;
	@Getter(value = AccessLevel.PROTECTED)
	private Currency currency;
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	public FundsRepository(Currency currency) {
		Optional.ofNullable(currency).orElseThrow(() -> new IllegalArgumentException("Currency is invalid: " + currency));
		this.currency = currency;
		addPropertyChangeListener(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	public BigDecimal calculateDenominations(List<T> denominations) {
		BigDecimal zero = BigDecimal.ZERO;
		
		return Optional.ofNullable(denominations).map(denoms -> {
			this.denominations = denominations;
			BigDecimal fundsTotal = denominations.stream()
					.filter(Objects::nonNull)
					.map(Denomination::getAmount)
					.reduce(zero, BigDecimal::add);
			
			updateFundsTotal(Optional.ofNullable(fundsTotal).orElse(zero));
			return fundsTotalValue;
		}).orElseGet(() -> {
			updateFundsTotal(zero);
			return zero;
		});
		
	}
	
	private void updateFundsTotal(BigDecimal fundsTotalValue) {
		this.fundsTotalValue = fundsTotalValue;
		support.firePropertyChange("fundsTotalValue", this.fundsTotalValue, fundsTotalValue);
	}
	
	public final void dispenseFunds() {
		if(hasFunds()) {
			displayBoxTotal(fundsTotalValue);
			
			Map<String, Long> denomGroups = denominations.stream()
					.collect(Collectors.groupingBy(denom -> denom.getCommonName(), Collectors.counting()));
			String denomCountMessage = denomGroups.entrySet().stream().map(this::buildDenomCountMessage).collect(Collectors.joining(", "));
			log.info("Dispensed {}", denomCountMessage);
			
			calculateDenominations(Collections.emptyList());
		}
	}

	protected abstract void displayBoxTotal(BigDecimal total);
	
	private boolean hasFunds() {
		return BigDecimal.ZERO.compareTo(fundsTotalValue) == -1;
	}

	private String buildDenomCountMessage(Entry<String, Long> entry) {
		return new StringBuilder(entry.getValue().toString()).append(" ").append(entry.getKey()).toString();
	}
	
}
