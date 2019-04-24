package com.ihg.soda.config;

import java.util.Currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ihg.soda.api.model.BeverageDetail;
import com.ihg.soda.api.model.entity.Beverage;
import com.ihg.soda.api.vending.SodaMachine;
import com.ihg.soda.api.vending.VendingMachine;

@Configuration
public class SodaMachineConfiguration {
	
	@Autowired
	private SodaMachineConfigurationProperties sodaMachineConfigProps;
	
	@Bean
	public VendingMachine<Beverage, BeverageDetail> sodaMachine() {
		SodaMachine machine = new SodaMachine();
		machine.setDefaultCurrency(Currency.getInstance(sodaMachineConfigProps.getCurrencyCode()));
		machine.setMaxUnitsPerProduct(sodaMachineConfigProps.getMaxCount());
		machine.setNumberOfProductQueues(sodaMachineConfigProps.getQueues());
		return machine;
	}
	
}
