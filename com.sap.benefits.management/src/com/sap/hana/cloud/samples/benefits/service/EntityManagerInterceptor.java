package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sap.hana.cloud.samples.benefits.persistence.common.PersistenceManager;

public class EntityManagerInterceptor implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			PersistenceManager.getInstance().initEntityManagerProvider();
			chain.doFilter(request, response);
		} finally {
			PersistenceManager.getInstance().closeEntityManager();
		}
	}

	public void init(FilterConfig config) {
	}

	public void destroy() {
		PersistenceManager.getInstance().closeAll();
	}
}
