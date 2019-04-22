package com.ihg.soda.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ihg.soda.enums.Denominations;
import com.ihg.soda.enums.LiquidContainerTypes;
import com.ihg.soda.enums.ProductBrands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = Include.NON_EMPTY)
public class BeverageRequest {
	
	private ProductBrands brand;
	private LiquidContainerTypes containerType;
	private List<Denominations> cash;
	private ChargeCard chargeCard;

}
