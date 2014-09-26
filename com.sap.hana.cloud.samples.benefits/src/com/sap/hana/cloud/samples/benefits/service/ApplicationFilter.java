package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sap.hana.cloud.samples.benefits.odata.UserManager;

public class ApplicationFilter implements Filter {

	private static final String ADMINISTRATOR_ROLE = "Administrator"; //$NON-NLS-1$

	public ApplicationFilter() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String userId = null;
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			boolean isAdminUser = httpRequest.isUserInRole(ADMINISTRATOR_ROLE);
			Principal userPrincipal = httpRequest.getUserPrincipal();
			if (userPrincipal != null) {
				userId = userPrincipal.getName();
			}
			UserManager.setUserId(userId);
			UserManager.setIsUserAdmin(isAdminUser);

			// pass the request along the filter chain
			chain.doFilter(request, response);
		} finally {
			UserManager.cleanUp();
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
