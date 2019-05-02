package com.ihg.soda.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ihg.soda.api.model.entity.SaleTransaction;

@RepositoryRestResource(collectionResourceRel = "productTransactions", path = "productTransactions")
public interface SaleTransactionRepository extends CrudRepository<SaleTransaction, Long> {

}
