package com.ihg.soda.model.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ihg.soda.model.entity.ProductTransaction;

@RepositoryRestResource(collectionResourceRel = "productTranscations", path = "productTransactions")
public interface ProductTransactionRepository extends CrudRepository<ProductTransaction, Long> {

}
