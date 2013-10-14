package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;

public class OrderBean {

	@Expose
	private long campaignId;

	@Expose
	private long benefitTypeId;

	@Expose
	private long quantity;

	public long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}

	public long getBenefitTypeId() {
		return benefitTypeId;
	}

	public void setBenefitTypeId(long benefitTypeId) {
		this.benefitTypeId = benefitTypeId;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

}
