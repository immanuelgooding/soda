package com.ihg.soda.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ihg.soda.api.model.entity.ProductTransaction;

@RepositoryRestResource(collectionResourceRel = "productTranscations", path = "productTransactions")
public interface ProductTransactionRepository extends CrudRepository<ProductTransaction, Long> {

}
