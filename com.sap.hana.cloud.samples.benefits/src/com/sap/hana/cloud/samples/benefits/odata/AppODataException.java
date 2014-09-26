package com.sap.hana.cloud.samples.benefits.odata;

import org.apache.olingo.odata2.api.exception.ODataException;

public class AppODataException extends ODataException {

	private static final long serialVersionUID = 1L;

	public AppODataException(String message) {
		super(message);
	}

	public AppODataException(String message, Throwable cause) {
		super(message, cause);
	}

}
