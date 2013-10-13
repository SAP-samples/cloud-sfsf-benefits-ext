package com.sap.benefits.management.api.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.Order;
import com.sap.benefits.management.persistence.model.OrderDetails;

public class IBenefitsOrder {

	@Expose
	public long id;
	
	@Expose
	public BigDecimal orderPrice = BigDecimal.ZERO;
	
	@Expose
	public ICampaign campaign;
	
	@Expose
	List<IBenefitsOrderItem> orderItems = new ArrayList<>();

	public void init(Order order) {
		this.id = order.getId();
		this.orderPrice = order.getTotal();
		this.campaign = new ICampaign();
		this.campaign.init(order.getCampaign());
		final Map<Long,IBenefitsOrderItem> benefitsMap = new HashMap<Long, IBenefitsOrderItem>();
		for (OrderDetails orderItem: order.getOrderDetails()) {
			Benefit benefit = orderItem.getBenefitType().getBenefit();
			IBenefitsOrderItem benefitOrderItem = benefitsMap.get(benefit.getId());
			if (benefitOrderItem == null) {
				benefitOrderItem = new IBenefitsOrderItem();
				benefitOrderItem.initBenefitDetails(orderItem.getBenefitType().getBenefit());
				benefitsMap.put(benefit.getId(), benefitOrderItem);
			}
			benefitOrderItem.addBenefitItem(orderItem);
		}
		this.orderItems.clear();
		this.orderItems.addAll(benefitsMap.values());
	}
	
}
