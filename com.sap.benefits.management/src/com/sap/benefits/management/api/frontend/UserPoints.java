package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;

public class UserPoints {
	
	@Expose
	private long campaingId;
	
	@Expose
	private String campaignName;
	
	@Expose
	private long availablePoints;
	
	@Expose
	private String userId;

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public long getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(long availablePoints) {
		this.availablePoints = availablePoints;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getCampaingId() {
		return campaingId;
	}

	public void setCampaingId(long campaingId) {
		this.campaingId = campaingId;
	}
	
	

}
