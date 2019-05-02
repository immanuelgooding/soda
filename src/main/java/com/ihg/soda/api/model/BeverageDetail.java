package com.ihg.soda.api.model;

import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.ProductBrands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Complex type that represents just enough information
 * to facilitate and/or display a beverage selection.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeverageDetail {
	
	protected ProductBrands brand;
	private Long upc;
	private PackagingTypes containerType;
	
	public BeverageDetail(ProductBrands brand, PackagingTypes containerType) {
		this.brand = brand;
		this.containerType = containerType;
	}
}
