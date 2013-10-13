package com.sap.benefits.management.api.frontend;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.OrderDetails;

public class IBenefitsOrderItem {
	
	@Expose
	public IBenefitDetails benefitDetails;
	
	@Expose
	public List<IBenefitItem> benefitItems = new ArrayList<>();

	public void initBenefitDetails(Benefit benefit) {
		this.benefitDetails = new IBenefitDetails();
		this.benefitDetails.init(benefit);	
	}

	public void addBenefitItem(OrderDetails orderItem) {
		IBenefitItem item = new IBenefitItem();
		item.init(orderItem);
		this.benefitItems.add(item);
	}

}
