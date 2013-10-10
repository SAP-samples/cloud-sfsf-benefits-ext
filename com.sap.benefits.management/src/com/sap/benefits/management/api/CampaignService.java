package com.sap.benefits.management.api;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.User;

@Path("/campaigns")
public class CampaignService extends BaseService{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private CampaignDAO campaignDAO = new CampaignDAO();
	private UserDAO userDAO = new UserDAO();

	@GET
	@Path("/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Campaign> getCampaigns(){
		final User user = userDAO.getByUserId(getLoggedInUserId());
		final Collection<Campaign> campaigns = user.getCampaigns();
		
		return campaigns;
	}
	
	@POST
	@Path("/admin")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addCampaign(Campaign campaign){
		final User user = userDAO.getByUserId(getLoggedInUserId());
		if(campaignDAO.getByName(campaign.getName(), user) != null){
			return Response.status(Status.BAD_REQUEST).entity("Campaign with name \"" + campaign.getName() + "\" already exist").build();
		}else if(campaign.isActive() && !campaignDAO.canBeActive(campaign, user)){
			return Response.status(Status.BAD_REQUEST).entity("Another campaign is set as active").build();
		}
		
		final Campaign newCampaign = new Campaign();
		newCampaign.setName(campaign.getName());
		newCampaign.setStartDate(campaign.getStartDate());
		newCampaign.setStartDate(campaign.getEndDate());
		newCampaign.setOwner(user);
		newCampaign.setActive(campaign.isActive());
		
		campaignDAO.save(newCampaign);
		
		return Response.ok().build();
	}
	
	@POST
	@Path("/admin/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editCampaign(@PathParam("id") long id, Campaign campaign){
		final User user = userDAO.getByUserId(getLoggedInUserId());
		final Campaign camp = campaignDAO.getById(id);
		if(camp == null){
			return Response.status(Status.BAD_REQUEST).entity("Campaign does not exist").build();
		}else if(campaign.isActive() && !campaignDAO.canBeActive(campaign, user)){
			return Response.status(Status.BAD_REQUEST).entity("Another campaign is set as active").build();
		}
		
		camp.setName(campaign.getName());
		camp.setStartDate(campaign.getStartDate());
		camp.setEndDate(campaign.getEndDate());
		camp.setActive(campaign.isActive());
		
		campaignDAO.save(camp);
		return Response.ok().build();
	}
	
}
