package com.ihg.soda.api.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.ihg.soda.enums.LiquidContainerTypes;
import com.ihg.soda.enums.ProductTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
public class Beverage extends DispensableProduct {
	
	@Column
	private final ProductTypes productType = ProductTypes.DRINK;
	
	@Column
	@Enumerated(EnumType.STRING)
	private LiquidContainerTypes containerType;
	
}
