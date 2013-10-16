package com.sap.hana.cloud.samples.benefits.api.bean;

import com.google.gson.annotations.Expose;

public class OrderBean {

	@Expose
	public long campaignId;

	@Expose
	public long benefitTypeId;

	@Expose
	public long quantity;

}
