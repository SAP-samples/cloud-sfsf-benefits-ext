package com.sap.hana.cloud.samples.benefits.persistence.model;

import static com.sap.hana.cloud.samples.benefits.persistence.model.DBQueries.GET_USER_POINTS;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "USER_POINTS")
@NamedQueries({ @NamedQuery(name = GET_USER_POINTS, query = "select u from UserPoints u where u.user.userId = :userId and u.campaign.id = :campaignId") })
public class UserPoints implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;

	@Column(name = "USER_ID", insertable = false, updatable = false)
	private Long userId;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID")
	private Campaign campaign;

	@Column(name = "CAMPAIGN_ID", insertable = false, updatable = false)
	private Long campaignId;

	@Basic
	@Column(name = "ENTITLEMENT_POINTS")
	private Long entitlementPoints;

	@Basic
	@Column(name = "AVAILABLE_POINTS")
	private Long availablePoints;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (!user.getUserPoints().contains(this)) {
			user.addUserPoints(this);
		}
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
		if (!campaign.getUserPoints().contains(this)) {
			campaign.addUserPoints(this);
		}
	}

	public Long getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(Long availablePoints) {
		this.availablePoints = availablePoints;
	}

	public void addPoints(long points) {
		this.availablePoints += points;
	}

	public void subtractPoints(long points) {
		if ((this.availablePoints - points) < 0) {
			throw new IllegalArgumentException("Available user points can not be less than zero"); //$NON-NLS-1$
		}
		this.availablePoints -= points;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public Long getEntitlementPoints() {
		return entitlementPoints;
	}

	public void setEntitlementPoints(Long entitlementPoints) {
		this.entitlementPoints = entitlementPoints;
	}
}
