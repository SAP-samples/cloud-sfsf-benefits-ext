package com.sap.benefits.management.persistence.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "ORDERS_DETAILS")
public class OrderDetails implements IDBEntity {
	
	@Expose
	@Id
	@GeneratedValue
	private Long id;
	
	@Expose
	@Basic
	private Long quantity;
	
	@Expose
	@Basic
	@Column(name = "LAST_UPDATE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;

	@ManyToOne
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ORDER_ID")
	private Order order;

	@Expose
	@ManyToOne
	@JoinColumn(name = "BENEFIT_TYPE_ID", referencedColumnName = "TYPE_ID", insertable=false, updatable=false)
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

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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
