package com.ihg.soda.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ihg.soda.api.model.BeverageResponse;
import com.ihg.soda.exception.VendingMachineException;

@ControllerAdvice
public class VendingMachineExceptionHandler {

	@ExceptionHandler(VendingMachineException.class)
	public ResponseEntity<BeverageResponse> handleException(VendingMachineException vme) {
		BeverageResponse beverageResponse = new BeverageResponse(vme.getMessage());
		return new ResponseEntity<>(beverageResponse, HttpStatus.OK);
	}
	
}
