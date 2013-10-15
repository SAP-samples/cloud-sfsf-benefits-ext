package com.sap.benefits.management.services;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class AuthorizationFilter implements Filter {

	private WebResource[] webResources;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String loggedInUser = httpRequest.getRemoteUser();
		if (loggedInUser != null) {
			final StringBuilder path = new StringBuilder(httpRequest.getServletPath());
			String pathInfo = httpRequest.getPathInfo();
			if (pathInfo != null) {
				path.append(pathInfo);
			}
			for (WebResource webResource : webResources) {
				String protectedPath = webResource.getPath();
				if(protectedPath.lastIndexOf('/')+1 != protectedPath.length()){
					protectedPath += "/";
				}
				
				if(path.indexOf(protectedPath) == 0 || path.append('/').toString().equalsIgnoreCase(protectedPath)){
					for (String role : webResource.getRoles()) {
						if (!httpRequest.isUserInRole(role)) {
							((HttpServletResponse) response).sendError(403);
						}
					}
				}
			}
		} else {
			((HttpServletResponse) response).sendError(403);
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		String authConfig = config.getInitParameter("auth.constraints");
		Gson gson = new Gson();
		webResources = gson.fromJson(authConfig, WebResource[].class);
	}

	public void destroy() {

	}

}
