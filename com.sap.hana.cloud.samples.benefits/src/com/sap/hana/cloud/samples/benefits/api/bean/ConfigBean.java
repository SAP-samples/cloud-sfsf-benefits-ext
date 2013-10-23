package com.sap.hana.cloud.samples.benefits.api.bean;

import com.google.gson.annotations.Expose;

public class ConfigBean {
	
	@Expose
	public boolean showEmployeesTile;
	
	@Expose
	public boolean showBenefitsTile;
	
	@Expose
	public boolean showCampaignTile;
	
	@Expose
	public boolean showOrderTile;
	
	@Expose
	public boolean isAdmin;
	
	public void initAdminConfiguration(){
		init(true, true, true, true, true);
//		init(false, false, false, true, false);
	}
	
	public void initEmployeeConfiguration(){
		init(false, false, false, true, false);
	}
	
	private void init(boolean employeesTile, boolean benefitsTile, boolean campaignTile, boolean orderTile, boolean isAdmin){
		this.showBenefitsTile = benefitsTile;
		this.showEmployeesTile = employeesTile;
		this.showCampaignTile = campaignTile;
		this.showOrderTile = orderTile;
		this.isAdmin = isAdmin;
	}

}
