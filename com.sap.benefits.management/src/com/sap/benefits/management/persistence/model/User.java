package com.sap.benefits.management.persistence.model;

import static com.sap.benefits.management.persistence.model.DBQueries.GET_ALL_USERS;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.gson.annotations.Expose;

@Entity
@Table(name = "USERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "USER_ID" }) })
@NamedQueries({ @NamedQuery(name = GET_ALL_USERS, query = "select e from User e") })
public class User implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "FIRST_NAME")
	@Expose
	private String firstName;

	@Basic
	@Column(name = "LAST_NAME")
	@Expose
	private String lastName;

	@Basic
	@Column(name = "USER_ID")
	@Expose
	private String userId;

	@Basic
	@Expose
	private String email;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, targetEntity = Order.class)
	private Collection<Order> orders;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, targetEntity = UserPoints.class)
	private Collection<UserPoints> userPoints;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collection<Order> getOrders() {
		if (orders == null) {
			orders = new ArrayList<>();
		}
		return orders;
	}

	public void setOrders(Collection<Order> orders) {
		this.orders = orders;
	}

	public void addOrder(Order order) {
		getOrders().add(order);
		if (order.getUser() != this) {
			order.setUser(this);
		}
	}

	public Collection<UserPoints> getUserPoints() {
		if (this.userPoints == null) {
			this.userPoints = new ArrayList<>();
		}
		return userPoints;
	}

	public void setUserPoints(Collection<UserPoints> userPoints) {
		this.userPoints = userPoints;
	}

	public void addUserPoints(UserPoints points) {
		getUserPoints().add(points);
		if (points.getUser() != this) {
			points.setUser(this);
		}
	}

}
