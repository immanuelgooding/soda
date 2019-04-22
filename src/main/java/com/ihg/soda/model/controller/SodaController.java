package com.ihg.soda.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ihg.soda.model.BeverageRequest;
import com.ihg.soda.model.BeverageResponse;
import com.ihg.soda.model.service.MachineManager;

@RestController
@RequestMapping("drink")
public class SodaController {
	
	@Autowired
	private MachineManager machineManager;
	
	@PostMapping("/purchase")
	@ResponseStatus(HttpStatus.OK)
	public BeverageResponse purchaseBeverage(@RequestBody BeverageRequest beverageRequest) {
		return machineManager.processRequest(beverageRequest);
	}

}
