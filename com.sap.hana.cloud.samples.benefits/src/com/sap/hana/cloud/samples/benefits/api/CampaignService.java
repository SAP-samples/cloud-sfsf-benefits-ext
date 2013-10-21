package com.sap.hana.cloud.samples.benefits.api;

import static java.text.MessageFormat.format;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sap.hana.cloud.samples.benefits.api.bean.CampaignBean;
import com.sap.hana.cloud.samples.benefits.api.bean.CampaignNameAvailabilityCheckResponseBean;
import com.sap.hana.cloud.samples.benefits.api.bean.StartCampaignResponseBean;
import com.sap.hana.cloud.samples.benefits.persistence.CampaignDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.Campaign;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

@Path("/campaigns")
public class CampaignService extends BaseService {

	private CampaignDAO campaignDAO = new CampaignDAO();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CampaignBean> getCampaigns() {
		final User user = getLoggedInUser();
		return CampaignBean.getList(user.getCampaigns());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addCampaign(CampaignBean campaign) {
		final User user = getLoggedInUser();
		if (campaignDAO.getByName(campaign.name, user) != null) {
			return createBadRequestResponse(format("Campaign with name \"{0}\" already exist", campaign.name));
		} else if (campaign.active && !campaignDAO.canBeActive(campaign.id, user)) {
			return createBadRequestResponse("Another campaign is set as active");
		}

		final Campaign newCampaign = new Campaign();
		newCampaign.setName(campaign.name);
		newCampaign.setOwner(user);
		newCampaign.setPoints(campaign.points);
		campaignDAO.saveNew(newCampaign);
		campaignDAO.setPointsToUsers(newCampaign);

		return createOkResponse();
	}
	
	@DELETE
	@Path("/{id}")
	public void deleteCampaign(@PathParam("id") long campaignId){
		campaignDAO.delete(campaignId);
	}

	@POST
	@Path("/edit/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editCampaign(@PathParam("id") long id, CampaignBean campaign) {
		final User user = getLoggedInUser();
		final Campaign camp = campaignDAO.getById(id);
		if (camp == null) {
			return createBadRequestResponse("Campaign does not exist");
		} else if (campaign.active && !campaignDAO.canBeActive(campaign.id, user)) {
			return createBadRequestResponse("Another campaign is set as active");
		}

		camp.setStartDate(campaign.startDate);
		camp.setEndDate(campaign.endDate);

		campaignDAO.save(camp);
		return createOkResponse();
	}

	@GET
	@Path("/start-possible/{campaignId}")
	@Produces(MediaType.APPLICATION_JSON)
	public StartCampaignResponseBean canStartCampaign(@PathParam("campaignId") long campaignId) {
		final StartCampaignResponseBean response = new StartCampaignResponseBean();
		response.setCampaignId(campaignId);

		final Campaign activeCampaign = campaignDAO.getActiveCampaign(getLoggedInUser());
		if (activeCampaign == null || activeCampaign.getId().equals(campaignId)) {
			response.setCanBeStarted(true);
		} else {
			response.setCanBeStarted(false);
			response.setStartedCampaignName(activeCampaign.getName());
		}

		return response;
	}

	@POST
	@Path("/start/{campaignId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response startCampaign(@PathParam("campaignId") long campaignId) {
		final StartCampaignResponseBean canStartCampaign = this.canStartCampaign(campaignId);
		if (canStartCampaign.getCanBeStarted()) {
			final Campaign campaign = campaignDAO.getById(campaignId);
			campaign.setActive(true);
			campaignDAO.save(campaign);
			return createOkResponse();
		}
		return createBadRequestResponse("Cannot start the campaign");
	}
	
	@POST
	@Path("/stop/{campaignId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response stopCampaign(@PathParam("campaignId") long campaignId) {
		final Campaign campaign = campaignDAO.getById(campaignId);
		if(campaign == null){
			return createBadRequestResponse("Campaign with that id does not exist");
		}
		
		campaign.setActive(false);
		campaignDAO.save(campaign);
		
		return createOkResponse();
	}
	
	@GET
	@Path("/check-name-availability")
	@Produces(MediaType.APPLICATION_JSON)
	public CampaignNameAvailabilityCheckResponseBean checkNameAvailability(@QueryParam("name") String name) {
		final CampaignNameAvailabilityCheckResponseBean response = new CampaignNameAvailabilityCheckResponseBean();
		final Campaign campaign = campaignDAO.getByName(name, getLoggedInUser());
		if(campaign == null){
			response.setAvailable(true);
		} else {
			response.setAvailable(false);
		}
		
		return response;
	}
}
