package com.ihg.soda.model.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ihg.soda.model.entity.Beverage;

@RepositoryRestResource(collectionResourceRel = "beverages", path = "beverages")
public interface BeverageRepository extends CrudRepository<Beverage, Long> {

	
}
