package com.sap.hana.cloud.samples.benefits.api.bean;

import com.google.gson.annotations.Expose;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;

public class BenefitTypeBean {

	@Expose
	public long id;

	@Expose
	public String name;

	@Expose
	public long value;

	public void init(BenefitType benefitType) {
		this.id = benefitType.getId();
		this.name = benefitType.getName();
		this.value = benefitType.getValue();
	}
	
	public static BenefitTypeBean get(BenefitType benefitType) {
		BenefitTypeBean result = new BenefitTypeBean();
		result.init(benefitType);
		return result;
	}
	
}
