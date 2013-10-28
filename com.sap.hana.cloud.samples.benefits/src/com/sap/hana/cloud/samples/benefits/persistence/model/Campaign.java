package com.sap.hana.cloud.samples.benefits.persistence.model;

import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_ACTIVE_CAMPAIGNS;
import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_CAMPAIGN_BY_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "CAMPAIGNS", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
@NamedQueries({ @NamedQuery(name = GET_CAMPAIGN_BY_NAME, query = "select c from Campaign c where c.name = :name and c.owner = :owner"),
        @NamedQuery(name = GET_ACTIVE_CAMPAIGNS, query = "select c from Campaign c where c.active = 1 and c.owner = :owner") })
public class Campaign implements IDBEntity {

    @Id
    @GeneratedValue
    @Column(name = "CAMPAIGN_ID")
    private Long id;

    @Basic
    private String name;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    private Date endDate;

    @Basic
    private long points;

    @Basic
    private boolean active;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "campaign", fetch = FetchType.LAZY, targetEntity = Order.class)
    private Collection<Order> orders;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "campaign", fetch = FetchType.LAZY, targetEntity = UserPoints.class)
    private Collection<UserPoints> userPoints;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Collection<Order> getOrders() {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        return orders;
    }

    public void addOrder(Order order) {
        getOrders().add(order);
        if (order.getCampaign() != this) {
            order.setCampaign(this);
        }

    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    public Collection<UserPoints> getUserPoints() {
        if (this.userPoints == null) {
            this.userPoints = new ArrayList<>();
        }
        return userPoints;
    }

    public void addUserPoints(UserPoints points) {
        getUserPoints().add(points);
        if (points.getCampaign() != this) {
            points.setCampaign(this);
        }
    }

    public void setUserPoints(Collection<UserPoints> userPoints) {
        this.userPoints = userPoints;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        if (!owner.getCampaigns().contains(this)) {
            owner.addCampaign(this);
        }
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

}
