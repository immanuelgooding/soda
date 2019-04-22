package com.ihg.soda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "soda-machine")
@PropertySource("classpath:application.yml")
@Data
public class SodaMachineConfigurationProperties {

	private Short queues;
	private Short maxCount;
	private String currencyCode;
}
