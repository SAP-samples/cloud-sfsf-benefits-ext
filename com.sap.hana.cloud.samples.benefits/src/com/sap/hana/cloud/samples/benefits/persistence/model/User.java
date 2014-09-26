package com.sap.hana.cloud.samples.benefits.persistence.model;

import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_USER_BY_EMAIL;
import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_USER_BY_USER_ID;

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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "USERS", uniqueConstraints = { @UniqueConstraint(columnNames = { "USER_ID" }) })
@NamedQueries({ @NamedQuery(name = GET_USER_BY_USER_ID, query = "select u from User u where u.userId = :userId"),
		@NamedQuery(name = GET_USER_BY_EMAIL, query = "select u from User u where u.email = :email") })
public class User implements IDBEntity {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@Basic
	@Column(name = "FIRST_NAME")
	private String firstName;

	@Basic
	@Column(name = "LAST_NAME")
	private String lastName;

	@Basic
	@Column(name = "USER_ID")
	private String userId;

	@Basic
	private String email;

	@ManyToOne
	@JoinColumn(name = "HR_USER_ID", referencedColumnName = "ID")
	private User hrManager;

	@Column(name = "HR_USER_ID", insertable = false, updatable = false)
	private Long hrId;

	@OneToMany(mappedBy = "hrManager", fetch = FetchType.EAGER, targetEntity = User.class)
	private List<User> employees;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, targetEntity = Order.class)
	private List<Order> orders;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, targetEntity = UserPoints.class)
	private List<UserPoints> userPoints;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER, targetEntity = Campaign.class)
	private List<Campaign> campaigns;

	public User() {

	}

	public User(String userId) {
		this.userId = userId;
	}

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

	public String getFullName() {
		return firstName + lastName;
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

	public List<Order> getOrders() {
		if (orders == null) {
			orders = new ArrayList<>();
		}
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void addOrder(Order order) {
		getOrders().add(order);
		if (order.getUser() != this) {
			order.setUser(this);
		}
	}

	public List<UserPoints> getUserPoints() {
		if (this.userPoints == null) {
			this.userPoints = new ArrayList<>();
		}
		return userPoints;
	}

	public void setUserPoints(List<UserPoints> userPoints) {
		this.userPoints = userPoints;
	}

	public void addUserPoints(UserPoints points) {
		getUserPoints().add(points);
		if (points.getUser() != this) {
			points.setUser(this);
		}
	}

	public User getHrManager() {
		return hrManager;
	}

	public void setHrManager(User hrManager) {
		this.hrManager = hrManager;
		if (!hrManager.getEmployees().contains(this)) {
			hrManager.addEmployee(this);
		}
	}

	public List<User> getEmployees() {
		if (this.employees == null) {
			this.employees = new ArrayList<>();
		}
		return employees;
	}

	public void addEmployee(User employee) {
		getEmployees().add(employee);
		if (employee.getHrManager() != this) {
			employee.setHrManager(this);
		}
	}

	public void setEmployees(List<User> employees) {
		this.employees = employees;
	}

	public List<Campaign> getCampaigns() {
		if (this.campaigns == null) {
			this.campaigns = new ArrayList<>();
		}
		return campaigns;
	}

	public void addCampaign(Campaign campaign) {
		getCampaigns().add(campaign);
		if (campaign.getOwner() != this) {
			campaign.setOwner(this);
		}
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	public Long getHrId() {
		return hrId;
	}

	public void setHrId(Long hrId) {
		this.hrId = hrId;
	}
}
