package com.sap.benefits.management.api.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.Order;
import com.sap.benefits.management.persistence.model.OrderDetails;

public class BenefitsOrderBean {

	@Expose
	public long id;
	
	@Expose
	public BigDecimal orderPrice = BigDecimal.ZERO;
	
	@Expose
	public CampaignBean campaign;
	
	@Expose
	List<BenefitsOrderItemBean> orderItems = new ArrayList<>();

	public void init(Order order) {
		this.id = order.getId();
		this.orderPrice = order.getTotal();
		this.campaign = new CampaignBean();
		this.campaign.init(order.getCampaign());
		final Map<Long,BenefitsOrderItemBean> benefitsMap = new HashMap<Long, BenefitsOrderItemBean>();
		for (OrderDetails orderItem: order.getOrderDetails()) {
			Benefit benefit = orderItem.getBenefitType().getBenefit();
			BenefitsOrderItemBean benefitOrderItem = benefitsMap.get(benefit.getId());
			if (benefitOrderItem == null) {
				benefitOrderItem = new BenefitsOrderItemBean();
				benefitOrderItem.initBenefitDetails(orderItem.getBenefitType().getBenefit());
				benefitsMap.put(benefit.getId(), benefitOrderItem);
			}
			benefitOrderItem.addBenefitItem(orderItem);
		}
		this.orderItems.clear();
		this.orderItems.addAll(benefitsMap.values());
	}
	
}
