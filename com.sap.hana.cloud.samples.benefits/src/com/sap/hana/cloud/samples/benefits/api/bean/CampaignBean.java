package com.sap.hana.cloud.samples.benefits.api.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;

public class CampaignBean {

	@Expose
	public long id;

	@Expose
	public String name;

	@Expose
	public long points;

	@Expose
	public boolean active = false;

	@Expose
	public Date startDate;

	@Expose
	public Date endDate;

	public void init(Campaign campaign) {
		this.id = campaign.getId();
		this.name = campaign.getName();
		this.points = campaign.getPoints();
		this.active = campaign.isActive();
		this.startDate = campaign.getStartDate();
		this.endDate = campaign.getEndDate();
	}

	public static CampaignBean get(Campaign campaign) {
		CampaignBean result = new CampaignBean();
		result.init(campaign);
		return result;
	}

	public static List<CampaignBean> getList(Collection<Campaign> campaignList) {
		List<CampaignBean> result = new ArrayList<>();
		for (Campaign campaign : campaignList) {
			result.add(get(campaign));
		}
		return result;
	}

}
