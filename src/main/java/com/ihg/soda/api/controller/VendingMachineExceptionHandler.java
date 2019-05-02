package com.ihg.soda.api.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ihg.soda.api.model.response.ProductResponse;
import com.ihg.soda.exception.VendingMachineException;

@ControllerAdvice
public class VendingMachineExceptionHandler {

	@ExceptionHandler(VendingMachineException.class)
	public ResponseEntity<ProductResponse> handleException(VendingMachineException vme) {
		String message = Optional.ofNullable(vme.getCause()).map(Throwable::getMessage).orElse(vme.getMessage());
		ProductResponse productResponse = new ProductResponse(message);
		return new ResponseEntity<>(productResponse, HttpStatus.OK);
	}
	
}
