package com.ihg.soda.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ihg.soda.api.model.request.CardRequest;
import com.ihg.soda.api.model.request.CashRequest;
import com.ihg.soda.api.model.response.ProductResponse;
import com.ihg.soda.api.service.ProductRequestManager;

@RestController
@RequestMapping("/beverage")
public class BeveragePurchaseController {
	
	@Autowired
	private ProductRequestManager requestManager;
	
	@PostMapping("/purchase/cash")
	@ResponseStatus(HttpStatus.OK)
	public ProductResponse purchaseBeverage(@RequestBody CashRequest cashRequest) {
		return requestManager.handleCashRequest(cashRequest);
	}
	
	@PostMapping("/purchase/card")
	@ResponseStatus(HttpStatus.OK)
	public ProductResponse purchaseBeverage(@RequestBody CardRequest cashRequest) {
		return requestManager.handleCardRequest(cashRequest);
	}

}
