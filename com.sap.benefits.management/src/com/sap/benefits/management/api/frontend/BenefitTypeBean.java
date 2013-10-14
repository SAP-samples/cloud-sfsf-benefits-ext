package com.sap.benefits.management.api.frontend;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

public class BenefitTypeBean {

	@Expose
	public long id;

	@Expose
	public String name;

	public BigDecimal value;
}
