package com.sap.benefits.management.persistence.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import com.sap.benefits.management.persistence.model.keys.UserPointsPrimaryKey;

@Entity
@IdClass(UserPointsPrimaryKey.class)
@Table(name = "USER_POINTS")
@NamedQueries({ 
	})
public class UserPoints implements IDBEntity {
	
	@GeneratedValue
	@Column(name="ID")
	private Long id;

	@ManyToOne
	@Id
    @JoinColumn(name="USER_ID", referencedColumnName="ID")
	private User user;
	
	@ManyToOne
	@Id
    @JoinColumn(name="CAMPAIGN_ID", referencedColumnName="CAMPAIGN_ID")
	private Campaign campaign;
	
	@Basic
	private Long points;
	
	@Basic
	@Column(name="AVAILABLE_POINTS")
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
		if(!user.getUserPoints().contains(this)){
			user.addUserPoints(this);
		}
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
		if(!campaign.getUserPoints().contains(this)){
			campaign.addUserPoints(this);
		}
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public Long getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(Long availablePoints) {
		this.availablePoints = availablePoints;
	}


}
