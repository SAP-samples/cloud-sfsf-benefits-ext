package com.sap.hana.cloud.samples.benefits.connectivity.http;

public class InvalidResponseException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidResponseException(String message) {
		super(message);
	}

}
