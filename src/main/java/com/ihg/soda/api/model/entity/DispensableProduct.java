package com.ihg.soda.api.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.ProductDetail;
import com.ihg.soda.enums.ProductStatuses;
import com.ihg.soda.enums.ProductTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class DispensableProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;
	
	@Embedded
	private ProductDetail productDetail;
	
	@Column
	@NotNull
	@Enumerated(EnumType.STRING)
	private ProductStatuses productStatus;
	
	@Embedded
	private FinancialExchange price;
	
	@Column
	@CreationTimestamp
	private LocalDateTime createdOn;
	
	@Column
	@UpdateTimestamp
	private LocalDateTime updatedOn;
	
	@Column
	@Enumerated(EnumType.STRING)
	private ProductTypes productType;
	
	@Transient
	@OneToOne(optional = true)
	@JoinColumn(name = "upc", referencedColumnName = "upc")
	private Promotion promotion;
	
}
