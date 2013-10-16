package com.sap.hana.cloud.samples.benefits.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sap.hana.cloud.samples.benefits.api.bean.BenefitDetailsBean;
import com.sap.hana.cloud.samples.benefits.persistence.BenefitDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.Benefit;

@Path("/benefits")
public class BenefitsService extends BaseService {
	
	final BenefitDAO benefitDAO = new BenefitDAO();
	/*
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Benefit> getAllBenefits(){
		return benefitDAO.getAll();
	}*/
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BenefitDetailsBean> getAllBnefitsNew(){
		List<Benefit> benfits = benefitDAO.getAll();
		List<BenefitDetailsBean> result = new ArrayList<>();
		for (Benefit benefit: benfits) {
			BenefitDetailsBean benefitDetails = BenefitDetailsBean.get(benefit);
			benefitDetails.initBenefitTypes(benefit);
			result.add(benefitDetails);
		}
		return result;
	}

}
