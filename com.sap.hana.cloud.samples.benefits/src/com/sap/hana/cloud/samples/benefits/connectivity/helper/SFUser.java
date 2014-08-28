package com.sap.hana.cloud.samples.benefits.connectivity.helper;

import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class SFUser {

	public String firstName;
	public String lastName;
	public String userId;
	public String email;
	public SFUser hr;

	public SFUser() {

	}

	public void write(User user) {
		user.setUserId(userId);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
	}

}
