package com.sap.benefits.management.connectivity.helper;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.User;

public class SFUser {

	@Expose
	public String firstName;

	@Expose
	public String lastName;

	@Expose
	public String userId;

	@Expose
	public String email;
	
	@Expose
	public SFUser hr;
	
	public void write(User user) {
		user.setUserId(userId);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
	}

}
