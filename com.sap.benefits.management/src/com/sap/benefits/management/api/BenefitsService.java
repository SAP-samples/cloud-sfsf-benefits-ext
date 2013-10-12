package com.sap.benefits.management.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.benefits.management.persistence.BenefitDAO;
import com.sap.benefits.management.persistence.model.Benefit;

@Path("/benefits")
public class BenefitsService extends BaseService {
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Benefit> getCampaigns(){
		final BenefitDAO benefitDAO = new BenefitDAO();
		return benefitDAO.getAll();
	}

}
