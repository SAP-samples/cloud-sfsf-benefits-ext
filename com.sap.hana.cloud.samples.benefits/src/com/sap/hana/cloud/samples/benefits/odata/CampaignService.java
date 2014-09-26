package com.sap.hana.cloud.samples.benefits.odata;

import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportEntitySets.CAMPAIGNS;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.ADD_CAMPAIGN;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.CAN_START_CAMPAIGN;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.DELETE_CAMPAIGN;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.EDIT_CAMPAIGN;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.HR_CAMPAIGNS;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.START_CAMPAIGN;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.STOP_CAMPAIGN;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames.USER_CAMPAIGNS;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.CAMPAIGN_ID;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.CAMPAIGN_NAME;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.NAME;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.START_DATE;
import static org.apache.olingo.odata2.api.annotation.edm.EdmType.DATE_TIME;
import static org.apache.olingo.odata2.api.annotation.edm.EdmType.INT64;
import static org.apache.olingo.odata2.api.annotation.edm.EdmType.STRING;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.HttpMethod;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hana.cloud.samples.benefits.odata.beans.StartCampaignDetails;
import com.sap.hana.cloud.samples.benefits.persistence.UserPointsDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class CampaignService extends ODataService {

	@EdmFunctionImport(name = USER_CAMPAIGNS, entitySet = CAMPAIGNS, returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<Campaign> getUserCampaigns() {
		final User currentUser = getLoggedInUser();
		return currentUser.getHrManager().getCampaigns();
	}

	@EdmFunctionImport(name = HR_CAMPAIGNS, entitySet = CAMPAIGNS, returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<Campaign> getHrCampaigns() {
		final User currentUser = getLoggedInUser();
		return currentUser.getCampaigns();
	}

	@EdmFunctionImport(name = START_CAMPAIGN, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean startCampaign(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = INT64) Long campaignId) {
		final StartCampaignDetails startCampaignDetails = this.canStartCampaign(campaignId);
		if (startCampaignDetails.getCanBeStarted()) {
			final Campaign campaign = campaignDAO.getById(campaignId);
			campaign.setActive(true);
			campaignDAO.save(campaign);
			return true;
		}
		return false;
	}

	@EdmFunctionImport(name = CAN_START_CAMPAIGN, returnType = @ReturnType(type = Type.COMPLEX, isCollection = false), httpMethod = HttpMethod.GET)
	public StartCampaignDetails canStartCampaign(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = INT64) Long campaignId) {
		final StartCampaignDetails response = new StartCampaignDetails();
		response.setCampaignId(campaignId);

		Campaign compaign = campaignDAO.getById(campaignId);
		final boolean isValidCampaign = this.isValidCampaign(compaign);

		final Campaign activeCampaign = campaignDAO.getActiveCampaign(getLoggedInUser());
		final boolean canBeStarted = activeCampaign == null || activeCampaign.getId().equals(campaignId);
		if (isValidCampaign && canBeStarted) {
			response.setCanBeStarted(true);
		} else {
			response.setCanBeStarted(false);
			if (activeCampaign != null) {
				response.setStartedCampaignName(activeCampaign.getName());
			}
		}

		return response;
	}

	@EdmFunctionImport(name = STOP_CAMPAIGN, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean stopCampaign(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = INT64) Long campaignId) throws ODataException {
		final Campaign campaign = campaignDAO.getById(campaignId);
		if (campaign == null) {
			throw new ODataException("Campaign with this name does not exist"); //$NON-NLS-1$
		}

		campaign.setActive(false);
		campaignDAO.save(campaign);

		return true;
	}

	@EdmFunctionImport(name = ADD_CAMPAIGN, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean addCampaign(@EdmFunctionImportParameter(name = NAME, type = STRING) String campaignName) throws ODataException {
		final User user = getLoggedInUser();
		if (campaignDAO.getByCaseInsensitiveName(campaignName, user) != null) {
			throw new ODataException("Campaign with this name already exist"); //$NON-NLS-1$
		}

		final Campaign newCampaign = new Campaign();
		newCampaign.setName(campaignName);
		newCampaign.setOwner(user);
		campaignDAO.saveNew(newCampaign);
		try {
			new UserPointsDAO().createCampaignUserPoints(newCampaign);
		} catch (IOException ex) {
			campaignDAO.delete(newCampaign.getId());
			throw new ODataException("Failed to create user points", ex); //$NON-NLS-1$
		}
		return true;
	}

	@EdmFunctionImport(name = DELETE_CAMPAIGN, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.DELETE)
	public boolean deleteCampaign(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = INT64) Long campaignId) throws ODataException {
		try {
			campaignDAO.delete(campaignId);
			return true;
		} catch (IllegalArgumentException ex) {
			throw new ODataException("Error occur while deleting campaign", ex); //$NON-NLS-1$
		}
	}

	private boolean isValidCampaign(Campaign campaign) {
		if (campaign == null || campaign.getName() == null || campaign.getStartDate() == null || campaign.getEndDate() == null) {
			return false;
		}
		if (campaign.getStartDate().compareTo(campaign.getEndDate()) >= 0) {
			return false;
		}

		return true;
	}

	@EdmFunctionImport(name = EDIT_CAMPAIGN, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean editCampaign(@EdmFunctionImportParameter(name = START_DATE, type = DATE_TIME) Date startDate,
			@EdmFunctionImportParameter(name = "endDate", type = DATE_TIME) Date endDate,
			@EdmFunctionImportParameter(name = "campaignid", type = INT64) Long campaignId) throws ODataException {
		final Campaign selectedCampaign = campaignDAO.getById(campaignId);
		if (selectedCampaign == null) {
			throw new ODataException("Campaign does not exist"); //$NON-NLS-1$
		} else if (startDate == null || endDate == null || startDate.compareTo(endDate) >= 0) {
			throw new ODataException("Incorrect campaign dates"); //$NON-NLS-1$
		}

		selectedCampaign.setStartDate(startDate);
		selectedCampaign.setEndDate(endDate);

		campaignDAO.save(selectedCampaign);
		return true;
	}

	@EdmFunctionImport(name = "checkNameAvailability", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.GET)
	public boolean checkNameAvailability(@EdmFunctionImportParameter(name = CAMPAIGN_NAME, type = STRING) String campaignName) {
		final Campaign campaign = campaignDAO.getByCaseInsensitiveName(campaignName, getLoggedInUser());
		return campaign == null;
	}

}
