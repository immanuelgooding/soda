package com.ihg.soda.exception;

public class VendingMachineException extends RuntimeException {

	public VendingMachineException(String message) {
		super(message);
	}

	public VendingMachineException(Throwable t) {
		super(t);
	}
}
