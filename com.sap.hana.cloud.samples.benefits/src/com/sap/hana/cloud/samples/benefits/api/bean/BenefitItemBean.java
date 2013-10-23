package com.sap.hana.cloud.samples.benefits.api.bean;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.sap.hana.cloud.samples.benefits.persistence.model.OrderDetails;

public class BenefitItemBean {
	
	@Expose
	public long id;
	
	@Expose
	public String name;
	
	@Expose
	public long quantity;
	
	@Expose
	public BigDecimal itemValue;

	public void init(OrderDetails orderItem) {
//		this.id = orderItem.getId();
		this.name = orderItem.getBenefitType().getName();
		this.itemValue = orderItem.getBenefitType().getValue();
		this.quantity = orderItem.getQuantity();	
	}	
	
	
	public static BenefitItemBean get(OrderDetails orderItem) {
		BenefitItemBean result = new BenefitItemBean();
		result.init(orderItem);
		return result;
	}
	
}
