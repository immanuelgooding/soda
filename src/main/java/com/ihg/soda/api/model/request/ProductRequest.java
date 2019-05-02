package com.ihg.soda.api.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ihg.soda.enums.PackagingTypes;
import com.ihg.soda.enums.ProductBrands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonInclude(value = Include.NON_EMPTY)
public abstract class ProductRequest {
	
	private ProductBrands brand;
	private PackagingTypes packaging;

}
