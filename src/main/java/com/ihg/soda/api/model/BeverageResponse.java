package com.ihg.soda.api.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_NULL)
public class BeverageResponse {

	private BeverageDetail beverageDetail;
	@NotNull
	private String message;
	
	public BeverageResponse(String message) {
		this.message = message;
	}
}
