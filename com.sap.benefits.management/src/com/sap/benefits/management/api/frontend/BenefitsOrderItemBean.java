package com.sap.benefits.management.api.frontend;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.OrderDetails;

public class BenefitsOrderItemBean {
	
	@Expose
	public BenefitDetailsBean benefitDetails;
	
	@Expose
	public List<BenefitItemBean> benefitItems = new ArrayList<>();

	public void initBenefitDetails(Benefit benefit) {
		this.benefitDetails = BenefitDetailsBean.get(benefit);
	}

	public void addBenefitItem(OrderDetails orderItem) {
		this.benefitItems.add(BenefitItemBean.get(orderItem));
	}

}
