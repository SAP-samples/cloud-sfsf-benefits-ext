package com.sap.hana.cloud.samples.benefits.persistence.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ORDERS_DETAILS")
public class OrderDetails implements IDBEntity {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Basic
	private Long quantity;
	
	@ManyToOne(cascade= CascadeType.ALL)
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID")
	private Order order;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "BENEFIT_TYPE_ID", referencedColumnName = "TYPE_ID")
	private BenefitType benefitType;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public BenefitType getBenefitType() {
		return benefitType;
	}

	public void setBenefitType(BenefitType benefitType) {
		this.benefitType = benefitType;
		if(!benefitType.getOrders().contains(this)){
			benefitType.addOrder(this);
		}
	}
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
		if(!order.getOrderDetails().contains(this)){
			order.addOrderDetails(this);
		}
	}

}
