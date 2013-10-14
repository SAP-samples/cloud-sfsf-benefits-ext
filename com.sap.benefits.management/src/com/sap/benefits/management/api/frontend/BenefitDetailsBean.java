package com.sap.benefits.management.api.frontend;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;

public class BenefitDetailsBean {
	
	@Expose
	public long id;
	
	@Expose
	public String name;
	
	@Expose
	public String description;
	
	@Expose
	public String infoLink;
	
	@Expose
	public List<BenefitTypeBean> benefitTypes = new ArrayList<>();

	public void init(Benefit benefit) {
		this.id = benefit.getId();
		this.name = benefit.getName();
		this.description = benefit.getDescription();
		this.infoLink = benefit.getLink();
	}
	
}
