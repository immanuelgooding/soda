package com.ihg.soda.api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ihg.soda.api.model.FinancialExchange;
import com.ihg.soda.api.model.entity.DispensableProduct;
import com.ihg.soda.enums.ProductBrands;

@RepositoryRestResource(collectionResourceRel = "dispensableProducts", path = "dispensableProducts")
public interface DispensableProductRepository extends CrudRepository<DispensableProduct, Long> {
	
	@Modifying
	@Query(value = "update DispensableProduct p set p.price = ? where p.brand = ?", nativeQuery = true)
	int updateProductSetPriceForBrandNative(FinancialExchange price, ProductBrands brand);
	
	@Modifying
	@Query(value = "update DispensableProduct p set p.price = ?", nativeQuery = true)
	int updateProductSetPriceForAllBrandsNative(FinancialExchange price);
}
