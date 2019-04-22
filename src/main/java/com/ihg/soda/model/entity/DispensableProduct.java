package com.ihg.soda.model.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ihg.soda.enums.ProductBrands;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.model.FinancialExchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DispensableProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@Column
	@Enumerated(EnumType.STRING)
	private ProductBrands brand;
	
	@Column
	private Long upc;
	
	@Column
	@NotNull
	@Enumerated(EnumType.STRING)
	private ProductStatuses productStatus;
	
	@Embedded
	private FinancialExchange price;
	
	@Column
	@CreationTimestamp
	private LocalDate createdOn;
	
	@Column
	@UpdateTimestamp
	private LocalDate updatedOn;
	
}
