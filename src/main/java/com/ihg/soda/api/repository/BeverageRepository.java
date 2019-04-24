package com.ihg.soda.api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.entity.Beverage;
import com.ihg.soda.enums.ProductBrands;

@RepositoryRestResource(collectionResourceRel = "beverages", path = "beverages")
public interface BeverageRepository extends CrudRepository<Beverage, Long> {
	
	@Modifying
	@Query(value = "update Beverage b set b.price = ? where b.brand = ?", nativeQuery = true)
	int updateBeverageSetPriceForBrandNative(FinancialExchange price, ProductBrands brand);
	
	@Modifying
	@Query(value = "update Beverage b set b.price = ?", nativeQuery = true)
	int updateBeverageSetPriceForAllBrandsNative(FinancialExchange price);
}
