package com.sap.benefits.management.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WebResource {
	private String path;
	
	private List<String> roles;
	
	public WebResource() {

	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setRoles(String[] roles) {
		if (roles == null) {
			this.roles = Collections.emptyList();
		} else {
			this.roles = Arrays.asList(roles);
		}
	}
	
	public String getPath() {
		return path;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
}
