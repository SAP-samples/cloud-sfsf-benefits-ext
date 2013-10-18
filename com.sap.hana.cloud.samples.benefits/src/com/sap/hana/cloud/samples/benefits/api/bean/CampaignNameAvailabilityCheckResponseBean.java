package com.sap.hana.cloud.samples.benefits.api.bean;

import com.google.gson.annotations.Expose;

public class CampaignNameAvailabilityCheckResponseBean {
	
	@Expose
	private boolean isAvailable;

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

}
