package com.ihg.soda.api.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.ProductBrands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Complex type that represents just enough information
 * to facilitate and/or display a product selection.
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"upc"})
@Builder
@Embeddable
public class ProductDetail {
	
	@Column
	@Enumerated(EnumType.STRING)
	private ProductBrands brand;
	
	@Column
	@Enumerated(EnumType.STRING)
	private PackagingTypes packaging;
	
	@Column
	private Long upc;
	
	public ProductDetail(ProductBrands brand, PackagingTypes packaging) {
		this.brand = brand;
		this.packaging = packaging;
	}
}
