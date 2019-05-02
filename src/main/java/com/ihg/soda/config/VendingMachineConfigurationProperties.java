package com.ihg.soda.config;

import java.util.Currency;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "vending-machine")
@PropertySource("classpath:application.yml")
@Data
public class VendingMachineConfigurationProperties {

	private Short queues;
	private Short maxCount;
	private String currencyCode;
	@Setter(value = AccessLevel.NONE)
	private Currency currency;
	
	@PostConstruct
	public void initialize() {
		Optional.of(currencyCode).ifPresent(code -> currency = Currency.getInstance(code));
	}
}
