package com.sap.benefits.management.api.frontend;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.BenefitType;

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
	
	public void initBenefitTypes(Benefit benefit) {
		this.benefitTypes.clear();
		for (BenefitType benefitType: benefit.getTypes()) {
			BenefitTypeBean benefitTypeBean = BenefitTypeBean.get(benefitType);
			this.benefitTypes.add(benefitTypeBean);
		}		
	}
	
	public static BenefitDetailsBean get(Benefit benefit) {
		BenefitDetailsBean result = new BenefitDetailsBean();
		result.init(benefit);
		return result;
	}
	
}
