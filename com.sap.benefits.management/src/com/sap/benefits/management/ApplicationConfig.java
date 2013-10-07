package com.sap.benefits.management;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.sap.benefits.management.api.TestService;


public class ApplicationConfig extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public ApplicationConfig() {
        singletons.add(new TestService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
