package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sap.hana.cloud.samples.benefits.connectivity.http.ECAPISession;
import com.sap.hana.cloud.samples.benefits.odata.UserManager;

public class ApplicationFilter implements Filter {

	public ApplicationFilter() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String userId = null;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		try {
			loadECAPISession(httpRequest.getSession());
			
			Principal userPrincipal = httpRequest.getUserPrincipal();
			if (userPrincipal != null) {
				userId = userPrincipal.getName();
				boolean isAdminUser = httpRequest.isUserInRole(ApplicationRoles.ADMINISTRATOR_ROLE);								
				UserManager.setUserId(userId);
				UserManager.setIsUserAdmin(isAdminUser);
				// pass the request along the filter chain
				chain.doFilter(request, response);
			}
		} finally {
			UserManager.cleanUp();
			storeECAPISession(httpRequest.getSession());
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}
	
	private void loadECAPISession(HttpSession httpSession) {
		Map<String, String> sessionHeaders = (Map<String, String>) httpSession.getAttribute("ECAPI.Headers");
		if (sessionHeaders != null) {
			ECAPISession.getInstance().updateSessionHeaders(sessionHeaders);
		}
	}
	
	private void storeECAPISession(HttpSession httpSession) {
		ECAPISession ecapiSession = ECAPISession.getInstance();
		httpSession.setAttribute("ECAPI.Headers", ecapiSession.getSessionHeaders());
		ecapiSession.cleanUp();
	}

}
