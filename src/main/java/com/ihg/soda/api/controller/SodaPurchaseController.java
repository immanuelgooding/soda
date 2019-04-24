package com.ihg.soda.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ihg.soda.api.model.BeverageRequest;
import com.ihg.soda.api.model.BeverageResponse;
import com.ihg.soda.api.service.MachineManager;

@RestController
@RequestMapping("drink")
public class SodaPurchaseController {
	
	@Autowired
	private MachineManager machineManager;
	
	@PostMapping("/purchase")
	@ResponseStatus(HttpStatus.OK)
	public BeverageResponse purchaseBeverage(@RequestBody BeverageRequest beverageRequest) {
		return machineManager.processRequest(beverageRequest);
	}

}
