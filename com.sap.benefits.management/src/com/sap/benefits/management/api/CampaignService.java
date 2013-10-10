package com.sap.benefits.management.api;

import java.sql.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.model.Campaign;

@Path("/campaigns")
public class CampaignService {

	@GET
	public Response getCampaigns(){
//		User user = getLogedInUser();
//		final List<Campaign> campaigns = user.getHrManager().getCampaigns();
		
		final CampaignDAO dao = new CampaignDAO();
		final List<Campaign> campaigns = dao.getAll();
		
		final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		
		return Response.ok().entity(gson.toJson(campaigns)).build();
	}
	
	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addCampaign(CampaignBean campaign){
		final Campaign newCampaign = new Campaign();
		System.out.println(campaign.name);
//		newCampaign.setName(campaign.name);
//		newCampaign.setStartDate(campaign.startDate);
//		newCampaign.setStartDate(campaign.endDate);
//		
		final CampaignDAO dao = new CampaignDAO();
//		dao.saveNew(newCampaign);
		
	}
	
	public class CampaignBean{
		@Expose
		public String name;
		@Expose
		public Date startDate;
		@Expose
		public Date endDate;
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
	}

}
