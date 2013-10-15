package com.sap.benefits.management.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.sap.benefits.management.api.BenefitsService;
import com.sap.benefits.management.api.CampaignService;
import com.sap.benefits.management.api.OrderService;
import com.sap.benefits.management.api.SystemService;
import com.sap.benefits.management.api.UserService;
import com.sap.benefits.management.api.utils.GsonMessageBodyHandler;


public class ApplicationConfig extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public ApplicationConfig() {
    	singletons.add(new GsonMessageBodyHandler<>());
		singletons.add(new UserService());
    	singletons.add(new SystemService());
    	singletons.add(new CampaignService());
        singletons.add(new BenefitsService());
        singletons.add(new OrderService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
