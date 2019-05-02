package com.ihg.soda.web.model;

import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.ProductBrands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeverageRequestModel {

	private ProductBrands brand;
	private PackagingTypes packaging;
	
	private String coinName;
	private String bankNoteName;
	private int coinQuantity;
	private int bankNoteQuantity;
	
	private String cardName;
	private Long cardNumber;
	private String expiryMonth;
	private Short expiryYear;
	private String provider;

}
