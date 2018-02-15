package com.sap.hana.cloud.samples.benefits.persistence.model;

import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_USER_ALL_ORDERS;
import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_USER_ORDERS_FOR_CAMPAIGN;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ORDERS")
@NamedQueries({ @NamedQuery(name = GET_USER_ORDERS_FOR_CAMPAIGN, query = "select o from Order o where o.user = :user and o.campaign = :campaign"),
		@NamedQuery(name = GET_USER_ALL_ORDERS, query = "select o from Order o where o.user = :user") })
public class Order implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ORDER_ID")
	private Long id;

	@Basic
	@Column
	private long total;

	@ManyToOne
	@JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID")
	private Campaign campaign;

	@Column(name = "CAMPAIGN_ID", insertable = false, updatable = false)
	private Long campaignId;

	@ManyToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;

	@Column(name = "USER_ID", insertable = false, updatable = false)
	private Long userId;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.EAGER, targetEntity = OrderDetails.class)
	private List<OrderDetails> orderDetails;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getTotal() {
		return total;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
		if (!campaign.getOrders().contains(this)) {
			campaign.getOrders().add(this);
		}
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (!user.getOrders().contains(this)) {
			user.addOrder(this);
		}
	}

	public List<OrderDetails> getOrderDetails() {
		if (this.orderDetails == null) {
			this.orderDetails = new ArrayList<>();
		}
		return orderDetails;
	}

	public void addOrderDetails(OrderDetails details) {
		getOrderDetails().add(details);
		this.total += details.getQuantity() * details.getBenefitType().getValue();

		if (details.getOrder() != this) {
			details.setOrder(this);
		}
	}

	public void removeOrderDetails(OrderDetails details) {
		final long orderTotal = details.getQuantity() * details.getBenefitType().getValue();
		if ((this.total - orderTotal) < 0) {
			throw new IllegalArgumentException("Order total value can not be less than zero"); //$NON-NLS-1$
		}
		getOrderDetails().remove(details);
		this.total -= orderTotal;
	}

	public void setOrderDetails(List<OrderDetails> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
