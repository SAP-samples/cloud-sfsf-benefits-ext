package com.sap.hana.cloud.samples.benefits.odata;

import java.util.Date;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.HttpMethod;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;
import org.apache.olingo.odata2.api.exception.ODataException;

import com.sap.hana.cloud.samples.benefits.commons.StartCampaignDetails;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class CampaignFunctionImport extends ODataService {

	@EdmFunctionImport(name = "userCampaigns", entitySet = "Campaigns", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<Campaign> getUserCampaigns() {
		final User currentUser = getLoggedInUser();
		return currentUser.getHrManager().getCampaigns();
	}

	@EdmFunctionImport(name = "hrCampaigns", entitySet = "Campaigns", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<Campaign> getHrCampaigns() {
		final User currentUser = getLoggedInUser();
		return currentUser.getCampaigns();
	}

	@EdmFunctionImport(name = "startCampaign", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean startCampaign(@EdmFunctionImportParameter(name = "campaignId", type = EdmType.INT64) Long campaignId) {
		final StartCampaignDetails startCampaignDetails = this.canStartCampaign(campaignId);
		if (startCampaignDetails.getCanBeStarted()) {
			final Campaign campaign = campaignDAO.getById(campaignId);
			campaign.setActive(true);
			campaignDAO.save(campaign);
			return true;
		}
		return false;
	}

	@EdmFunctionImport(name = "canStartCampaign", returnType = @ReturnType(type = Type.COMPLEX, isCollection = false), httpMethod = HttpMethod.GET)
	public StartCampaignDetails canStartCampaign(@EdmFunctionImportParameter(name = "campaignId", type = EdmType.INT64) Long campaignId) {
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

	@EdmFunctionImport(name = "stopCampaign", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean stopCampaign(@EdmFunctionImportParameter(name = "campaignId", type = EdmType.INT64) Long campaignId) throws ODataException {
		final Campaign campaign = campaignDAO.getById(campaignId);
		if (campaign == null) {
			throw new ODataException("Campaign with this name does not exist"); //$NON-NLS-1$
		}

		campaign.setActive(false);
		campaignDAO.save(campaign);

		return true;
	}

	@EdmFunctionImport(name = "addCampaign", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean addCampaign(@EdmFunctionImportParameter(name = "name", type = EdmType.STRING) String campaignName,
			@EdmFunctionImportParameter(name = "points", type = EdmType.INT64) Long campaignPoints) throws ODataException {
		final User user = getLoggedInUser();
		if (campaignDAO.getByCaseInsensitiveName(campaignName, user) != null) {
			throw new ODataException("Campaign with this name already exist"); //$NON-NLS-1$

		} else if (campaignPoints <= 0) {
			throw new ODataException("Points should be positive number, greater than zero"); //$NON-NLS-1$
		}

		final Campaign newCampaign = new Campaign();
		newCampaign.setName(campaignName);
		newCampaign.setOwner(user);
		newCampaign.setPoints(campaignPoints);
		campaignDAO.saveNew(newCampaign);
		campaignDAO.setPointsToUsers(newCampaign);
		return true;
	}

	@EdmFunctionImport(name = "deleteCampaign", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.DELETE)
	public boolean deleteCampaign(@EdmFunctionImportParameter(name = "campaignId", type = EdmType.INT64) Long campaignId) throws ODataException {
		try {
			campaignDAO.delete(campaignId);
			return true;
		} catch (IllegalArgumentException ex) {
			throw new ODataException("Error occur while deleting campaign", ex); //$NON-NLS-1$
		}
	}

	private boolean isValidCampaign(Campaign campaign) {
		if (campaign == null || campaign.getName() == null || campaign.getStartDate() == null || campaign.getEndDate() == null
				|| campaign.getPoints() <= 0) {
			return false;
		}
		if (campaign.getStartDate().compareTo(campaign.getEndDate()) >= 0) {
			return false;
		}

		return true;
	}

	@EdmFunctionImport(name = "editCampaign", returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.POST)
	public boolean editCampaign(@EdmFunctionImportParameter(name = "startDate", type = EdmType.DATE_TIME) Date startDate,
			@EdmFunctionImportParameter(name = "endDate", type = EdmType.DATE_TIME) Date endDate,
			@EdmFunctionImportParameter(name = "campaignid", type = EdmType.INT64) Long campaignId) throws ODataException {
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
	public boolean checkNameAvailability(@EdmFunctionImportParameter(name = "campaignName", type = EdmType.STRING) String campaignName) {
		final Campaign campaign = campaignDAO.getByCaseInsensitiveName(campaignName, getLoggedInUser());
		return campaign == null;
	}

}
