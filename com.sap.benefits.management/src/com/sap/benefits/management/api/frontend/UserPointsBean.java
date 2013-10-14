package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.UserPoints;

public class UserPointsBean {
	
	@Expose
	public long campaingId;
	
	@Expose
	public long availablePoints;
	
	@Expose
	public String userId;

	public void init(com.sap.benefits.management.persistence.model.UserPoints userPoints) {
		this.campaingId = userPoints.getCampaign().getId();
		this.userId = userPoints.getUser().getUserId();
		this.availablePoints = userPoints.getAvailablePoints();		
	}
	
	public static UserPointsBean get(UserPoints userPoints) {
		UserPointsBean result = new UserPointsBean();
		result.init(userPoints);
		return result;
	}

}
