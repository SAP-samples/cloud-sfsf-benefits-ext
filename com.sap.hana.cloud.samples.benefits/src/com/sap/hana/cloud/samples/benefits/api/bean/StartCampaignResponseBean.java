package com.sap.hana.cloud.samples.benefits.api.bean;

import com.google.gson.annotations.Expose;

public class StartCampaignResponseBean {
	
	@Expose
	private boolean canBeStarted;
	
	@Expose
	private long campaignId;
	
	@Expose
	private String startedCampaignName;
	
	public boolean getCanBeStarted() {
		return canBeStarted;
	}
	
	public void setCanBeStarted(boolean canBeStarted) {
		this.canBeStarted = canBeStarted;
	}
	
	public long getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getStartedCampaignName() {
		return startedCampaignName;
	}
	
	public void setStartedCampaignName(String startedCampaignName) {
		this.startedCampaignName = startedCampaignName;
	}

}
