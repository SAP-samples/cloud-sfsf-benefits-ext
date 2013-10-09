package com.sap.benefits.management.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.benefits.management.csv.dataimport.BenefitsDataImporter;
import com.sap.benefits.management.persistence.BenefitDAO;
import com.sap.benefits.management.persistence.BenefitTypeDAO;
import com.sap.benefits.management.persistence.CampaignDAO;
import com.sap.benefits.management.persistence.OrderDAO;
import com.sap.benefits.management.persistence.OrderDetailDAO;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.UserPointsDAO;
import com.sap.benefits.management.persistence.model.Benefit;
import com.sap.benefits.management.persistence.model.BenefitType;
import com.sap.benefits.management.persistence.model.Campaign;
import com.sap.benefits.management.persistence.model.Order;
import com.sap.benefits.management.persistence.model.OrderDetails;
import com.sap.benefits.management.persistence.model.User;
import com.sap.benefits.management.persistence.model.UserPoints;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class TestService {

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
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String test() throws NamingException {
		cleanDB();
		
		final CampaignDAO campaignDAO = new CampaignDAO();
		final UserDAO userDAO = new UserDAO();
		final OrderDAO ordersDAO = new OrderDAO();
		final BenefitDAO benefitDAO = new BenefitDAO();
		final UserPointsDAO userPointsDAO = new UserPointsDAO();
		
		final Benefit benefit = new Benefit();
		benefit.setName("Trapeza pass");
		benefit.setDescription("test description");
		benefit.setLink("http://www.google.com/");
		
		final BenefitType benefitType = new BenefitType();
		benefitType.setBenefit(benefit);
		benefitType.setActive(true);
		benefitType.setName("2 lv");
		benefitType.setValue(BigDecimal.valueOf(2));
		
		benefitDAO.saveNew(benefit);
		
		final User marin = new User();
		marin.setFirstName("Marin");
		marin.setLastName("Hadzhiev");
		
		userDAO.saveNew(marin);

		Campaign campaign = new Campaign();
		campaign.setActive(true);
		campaign.setStartDate(new Date(new java.util.Date().getTime()));
		campaign.setEndDate(new Date(new java.util.Date().getTime()));
		campaign.setName("Test Campaign");
		campaignDAO.saveNew(campaign);
		
		Order order = new Order();
		order.setTotal(BigDecimal.valueOf(100));
		order.setCampaign(campaign);
		order.setUser(marin);
		
		final OrderDetails orderDetails = new OrderDetails();
		orderDetails.setBenefitType(benefitType);
		orderDetails.setLastUpdateTime(new Date(new java.util.Date().getTime()));
		orderDetails.setOrder(order);
		orderDetails.setQuantity(Long.valueOf(3));
		
		ordersDAO.saveNew(order);
		
		final UserPoints userPoints = new UserPoints();
		userPoints.setAvailablePoints(Long.valueOf(100));
		userPoints.setPoints(Long.valueOf(300));
		userPoints.setUser(marin);
		userPoints.setCampaign(campaign);
		
		userPointsDAO.saveNew(userPoints);
		
		final Benefit b = benefitDAO.getByName("Trapeza pass");
		
		return b.getDescription();
	}
	
	
	@GET
	@Path("csv")
	@Produces(MediaType.APPLICATION_JSON)
	public String importDataTest() throws IOException {
//		cleanDB();
		
		final BenefitsDataImporter importer = new BenefitsDataImporter();
		importer.importData("/benefits.csv");
		
		return "ok";
	}
	
	
}