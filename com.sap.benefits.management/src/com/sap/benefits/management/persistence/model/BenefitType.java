package com.sap.benefits.management.persistence.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "BENEFIT_TYPE", uniqueConstraints={@UniqueConstraint(columnNames={"name", "BENEFIT_ID"})})
public class BenefitType {
	
	@Id
	@GeneratedValue
	@Column(name = "TYPE_ID")
	private Long id;
	
	@Basic
	private String name;
	
	@Basic
	@Column(precision = 25, scale = 2)
	private BigDecimal value;
	
	@Basic
	private boolean active;

	@ManyToOne
	@JoinColumn(name = "BENEFIT_ID", referencedColumnName = "BENEFIT_ID")
	private Benefit benefit;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "benefitType", fetch = FetchType.LAZY, targetEntity = OrderDetails.class)
	private Collection<OrderDetails> orders;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Benefit getBenefit() {
		return benefit;
	}

	public void setBenefit(Benefit benefit) {
		this.benefit = benefit;
		if(!benefit.getTypes().contains(this)){
			benefit.addType(this);
		}
	}

	public Collection<OrderDetails> getOrders() {
		if(this.orders == null){
			this.orders = new ArrayList<>();
		}
		return orders;
	}
	
	public void addOrder(OrderDetails order){
		getOrders().add(order);
		if(order.getBenefitType() != this){
			order.setBenefitType(this);
		}
	}

	public void setOrders(Collection<OrderDetails> orders) {
		this.orders = orders;
	}
}
