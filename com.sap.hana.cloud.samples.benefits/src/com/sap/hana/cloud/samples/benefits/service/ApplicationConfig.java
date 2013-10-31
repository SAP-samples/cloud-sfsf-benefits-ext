package com.sap.hana.cloud.samples.benefits.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.sap.hana.cloud.samples.benefits.api.BenefitsService;
import com.sap.hana.cloud.samples.benefits.api.CampaignService;
import com.sap.hana.cloud.samples.benefits.api.OrderService;
import com.sap.hana.cloud.samples.benefits.api.SystemService;
import com.sap.hana.cloud.samples.benefits.api.UserService;
import com.sap.hana.cloud.samples.benefits.api.util.GsonMessageBodyHandler;

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
