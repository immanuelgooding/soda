/**
 * 
 */
package com.ihg.soda.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ihg.soda.model.BeverageDetail;
import com.ihg.soda.model.FinancialExchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class ProductTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@CreationTimestamp
	@Column
	private LocalDate createdOn;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
	private DispensableProduct product;
	
	@Embedded
	private FinancialExchange paymentTendered;
	
	@Column
	private BigDecimal changeAmount;
	
	@Transient
	private BeverageDetail beverageDetail;
	
}
