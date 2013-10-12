package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;

public class User {
	
	@Expose
	private String firstName;

	@Expose
	private String lastName;

	@Expose
	private String userId;

	@Expose
	private String email;
	
	@Expose
	private UserPoints activeCampaignBalance;
	
	public User(com.sap.benefits.management.persistence.model.User user) {
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.userId = user.getUserId();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserPoints getActiveCampaignBalance() {
		return activeCampaignBalance;
	}

	public void setActiveCampaignBalance(UserPoints activeCampaignBalance) {
		this.activeCampaignBalance = activeCampaignBalance;
	}
	
}
