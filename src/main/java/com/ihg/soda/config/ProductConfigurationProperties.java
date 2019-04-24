package com.ihg.soda.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.ihg.soda.api.model.BeverageDetail;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "inventory")
@PropertySource("classpath:application.yml")
@Data
public class ProductConfigurationProperties {

	private List<BeverageDetail> beverages;
}
