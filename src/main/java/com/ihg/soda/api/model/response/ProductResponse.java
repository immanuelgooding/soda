package com.ihg.soda.api.model.response;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ihg.soda.api.model.ProductDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class ProductResponse {

	private ProductDetail productDetail;
	@NotNull
	private String message;
	
	public ProductResponse(String message) {
		this.message = message;
	}
}
