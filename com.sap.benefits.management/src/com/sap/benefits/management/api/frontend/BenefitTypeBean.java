package com.sap.benefits.management.api.frontend;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.BenefitType;

public class BenefitTypeBean {

	@Expose
	public long id;

	@Expose
	public String name;

	@Expose
	public BigDecimal value;

	public void init(BenefitType benefitType) {
		this.id = benefitType.getId();
		this.name = benefitType.getName();
		this.value = benefitType.getValue();
	}
	
}
