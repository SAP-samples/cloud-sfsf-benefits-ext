package com.sap.benefits.management.api.frontend;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;

public class IBenefitDetails {
	
	@Expose
	public long id;
	
	@Expose
	public String name;
	
	@Expose
	public String description;
	
	@Expose
	public String infoLink;

	public void init(Benefit benefit) {
		this.id = benefit.getId();
		this.name = benefit.getName();
		this.description = benefit.getDescription();
		this.infoLink = benefit.getLink();
	}
	
}
