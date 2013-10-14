package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.User;

public class UserBean {
	
	@Expose
	public String firstName;

	@Expose
	public String lastName;

	@Expose
	public String userId;

	@Expose
	public String email;
	
	@Expose
	public UserPointsBean activeCampaignBalance;
	
	public void init(User user) {
		this.email = user.getEmail();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.userId = user.getUserId();
	}
	
	public void setActiveCampaignBalance(com.sap.benefits.management.persistence.model.UserPoints userPoints) {
		this.activeCampaignBalance = new UserPointsBean();
		this.activeCampaignBalance.init(userPoints);
	}
	

}
