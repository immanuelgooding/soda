package com.ihg.soda.web.model;

import com.ihg.soda.enums.Denominations;
import com.ihg.soda.enums.LiquidContainerTypes;
import com.ihg.soda.enums.ProductBrands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeverageRequestModel {

	private ProductBrands brand;
	private LiquidContainerTypes containerType;
	private Denominations denomination;
	private Integer denominationQuantity;

	private String cardName;
	private Long cardNumber;
	private String expiryMonth;
	private Short expiryYear;
	private String provider;
}
