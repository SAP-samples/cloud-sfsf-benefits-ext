package com.sap.hana.cloud.samples.benefits.connectivity.http.headers;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;

public class AuthenticationHeaderImpl implements AuthenticationHeader {
	
	private final String name;
	private final String value;
	
	public AuthenticationHeaderImpl(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
