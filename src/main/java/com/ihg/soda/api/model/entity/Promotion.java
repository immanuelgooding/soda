package com.ihg.soda.api.model.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Promotion {
	
	@Id
	private Long upc;
	
	@Column
	private String promotionName;
	
	@Column
	private BigDecimal discountPercentage;
	
}
