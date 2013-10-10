package com.sap.benefits.management.api;

import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.benefits.management.csv.dataimport.BenefitsDataImporter;
import com.sap.benefits.management.persistence.BenefitDAO;
import com.sap.benefits.management.persistence.BenefitTypeDAO;
import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.OrderDAO;
import com.sap.benefits.management.persistence.OrderDetailDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.UserPointsDAO;

@Path("/system")
public class SystemService extends BaseService{
	
	private final Logger logger = LoggerFactory.getLogger(SystemService.class);
	
	@POST
	@Path("reset-db")
	public Response resetDatabase() {
		cleanDB();
		
		final BenefitsDataImporter benefitImporter = new BenefitsDataImporter();
		try {
			benefitImporter.importData("/benefits.csv");
		} catch (IOException e) {
			logger.error("Could not insert beneits data into DB", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.ok().build();
	}

	private void cleanDB(){
		final CampaignDAO campaignDAO = new CampaignDAO();
		final UserDAO userDAO = new UserDAO();
		final OrderDAO ordersDAO = new OrderDAO();
		final BenefitDAO benefitDAO = new BenefitDAO();
		final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
		final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
		final UserPointsDAO userPointsDAO = new UserPointsDAO();
		
		campaignDAO.deleteAll();
		userDAO.deleteAll();
		ordersDAO.deleteAll();
		benefitDAO.deleteAll();
		benefitTypeDAO.deleteAll();
		orderDetailDAO.deleteAll();
		userPointsDAO.deleteAll();
	}
	
}