package com.sap.hana.cloud.samples.benefits.validation.exception;

public class InvalidResponseBodyException extends InvalidResponseException {

	private static final long serialVersionUID = 1L;

	public InvalidResponseBodyException(String message) {
		super(message);
	}
}
