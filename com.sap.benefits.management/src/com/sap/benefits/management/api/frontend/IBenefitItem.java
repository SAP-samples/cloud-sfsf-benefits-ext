package com.sap.benefits.management.api.frontend;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.OrderDetails;

public class IBenefitItem {
	
	@Expose
	public String name;
	
	@Expose
	public long quantity;
	
	@Expose
	public BigDecimal itemValue;

	public void init(OrderDetails orderItem) {
		this.name = orderItem.getBenefitType().getName();
		this.itemValue = orderItem.getBenefitType().getValue();
		this.quantity = orderItem.getQuantity();	
	}
	
}
