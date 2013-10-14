package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Campaign;

public class CampaignBean {
	
	@Expose
	public long id;
	
	@Expose
	public String name;
	
	@Expose
	public long points;
	
	@Expose
	public boolean active = false;

	public void init(Campaign campaign) {
		this.id = campaign.getId();
		this.name = campaign.getName();
		this.points = campaign.getPoints();
		this.active = campaign.isActive();
	}

}
