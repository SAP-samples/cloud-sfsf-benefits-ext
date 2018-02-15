package com.sap.hana.cloud.samples.benefits.odata.beans;


public class StartCampaignDetails {

	private boolean canBeStarted;
	private long campaignId;
	private String startedCampaignName;

	public StartCampaignDetails() {

	}

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
