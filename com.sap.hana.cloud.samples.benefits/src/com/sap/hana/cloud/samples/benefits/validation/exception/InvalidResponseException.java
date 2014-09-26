package com.sap.hana.cloud.samples.benefits.validation.exception;

public class InvalidResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidResponseException(String message) {
		super(message);
	}
}
