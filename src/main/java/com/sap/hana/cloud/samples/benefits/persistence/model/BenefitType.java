package com.sap.hana.cloud.samples.benefits.persistence.model;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "BENEFIT_TYPE", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "BENEFIT_INFO_ID" }) })
public class BenefitType implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "TYPE_ID")
	private Long id;

	@Basic
	private String name;

	@Basic
	@Column
	private long value;

	@Basic
	private boolean active;

	@ManyToOne
	@JoinColumn(name = "BENEFIT_INFO_ID", referencedColumnName = "BENEFIT_INFO_ID")
	private BenefitInfo benefitInfo;

	@Column(name = "BENEFIT_INFO_ID", insertable = false, updatable = false)
	private Long benefitId;

	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "benefitType", fetch = FetchType.EAGER, targetEntity = OrderDetails.class)
	private List<OrderDetails> orders;

	@Override
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

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BenefitInfo getBenefitInfo() {
		return benefitInfo;
	}

	public Long getBenefitId() {
		return benefitId;
	}

	public void setBenefitId(Long benefitId) {
		this.benefitId = benefitId;
	}

	public void setBenefitInfo(BenefitInfo benefitInfo) {
		this.benefitInfo = benefitInfo;
		if (!benefitInfo.getTypes().contains(this)) {
			benefitInfo.addType(this);
		}
	}

	public List<OrderDetails> getOrders() {
		if (this.orders == null) {
			this.orders = new ArrayList<>();
		}
		return orders;
	}

	public void addOrder(OrderDetails order) {
		getOrders().add(order);
		if (order.getBenefitType() != this) {
			order.setBenefitType(this);
		}
	}

	public void setOrders(List<OrderDetails> orders) {
		this.orders = orders;
	}
}
