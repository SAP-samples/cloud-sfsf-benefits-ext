package com.sap.hana.cloud.samples.benefits.odata.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BenefitsAmount {

	@Expose
	@SerializedName("cust_targetPoints")
	private long targetPoints;

	@Expose
	@SerializedName("externalCode")
	private String userId;

	public BenefitsAmount() {
	};

	public BenefitsAmount(String userId, long targetPoints) {
		this.targetPoints = targetPoints;
		this.userId = userId;
	}

	public long getTargetPoints() {
		return targetPoints;
	}

	public void setTargetPoints(long targetPoints) {
		this.targetPoints = targetPoints;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
