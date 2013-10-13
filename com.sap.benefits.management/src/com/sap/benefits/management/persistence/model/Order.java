package com.sap.benefits.management.persistence.model;

import static com.sap.benefits.management.persistence.model.DBQueries.GET_USER_ORDERS_FOR_CAMPAIGN;
import static com.sap.benefits.management.persistence.model.DBQueries.GET_USER_ALL_ORDERS;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "ORDERS")
@NamedQueries({ 
	@NamedQuery(name = GET_USER_ORDERS_FOR_CAMPAIGN, query = "select o from Order o where o.user = :user and o.campaign = :campaign"),
	@NamedQuery(name = GET_USER_ALL_ORDERS, query = "select o from Order o where o.user = :user")
	})
public class Order implements IDBEntity {
	
	@Expose
	@Id
	@GeneratedValue
	@Column(name = "ORDER_ID")
	private Long id;
	
	@Expose
	@Basic
	@Column(precision = 25, scale = 2)
	private BigDecimal total;

	@Expose
	@ManyToOne
	@JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID")
	private Campaign campaign;
	
	@ManyToOne
    @JoinColumn(name="USER_ID", referencedColumnName="ID")
	private User user;
	
	@Expose
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY, targetEntity = OrderDetails.class)
	private Collection<OrderDetails> orderDetails;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
		if(!campaign.getOrders().contains(this)){
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
		if(!user.getOrders().contains(this)){
			user.addOrder(this);
		}
	}

	public Collection<OrderDetails> getOrderDetails() {
		if(this.orderDetails == null){
			this.orderDetails = new ArrayList<>();
		}
		return orderDetails;
	}
	
	public void addOrderDetails(OrderDetails details){
		getOrderDetails().add(details);
		if(details.getOrder() != this){
			details.setOrder(this);
		}
	}

	public void setOrderDetails(Collection<OrderDetails> orderDetails) {
		this.orderDetails = orderDetails;
	}

}
