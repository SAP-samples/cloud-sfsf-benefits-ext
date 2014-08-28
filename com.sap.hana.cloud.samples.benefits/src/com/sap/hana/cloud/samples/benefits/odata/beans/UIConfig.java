package com.sap.hana.cloud.samples.benefits.odata.beans;


public class UIConfig {

	public boolean showEmployeesTile;
	public boolean showBenefitsTile;
	public boolean showCampaignTile;
	public boolean showOrderTile;
	public boolean showInfoTile;

	public UIConfig() {

	}

	public void initAdminConfiguration() {
		init(true, true, true, true, true);
	}

	public void initEmployeeConfiguration() {
		init(false, true, false, true, true);
	}

	private void init(boolean employeesTile, boolean benefitsTile, boolean campaignTile, boolean orderTile, boolean infoTile) {
		this.showBenefitsTile = benefitsTile;
		this.showEmployeesTile = employeesTile;
		this.showCampaignTile = campaignTile;
		this.showOrderTile = orderTile;
		this.showInfoTile = infoTile;
	}

	public boolean isShowEmployeesTile() {
		return showEmployeesTile;
	}

	public boolean isShowBenefitsTile() {
		return showBenefitsTile;
	}

	public boolean isShowCampaignTile() {
		return showCampaignTile;
	}

	public boolean isShowOrderTile() {
		return showOrderTile;
	}

	public boolean isShowInfoTile() {
		return showInfoTile;
	}

}
