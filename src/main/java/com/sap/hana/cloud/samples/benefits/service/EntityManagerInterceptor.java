package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;

public class EntityManagerInterceptor implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
        	EntityManagerProvider.getInstance().initEntityManagerProvider();
            chain.doFilter(request, response);
        } finally {
            EntityManagerProvider.getInstance().closeEntityManager();
        }
    }

    public void init(FilterConfig config) {
    }

    public void destroy() {
    }
}
