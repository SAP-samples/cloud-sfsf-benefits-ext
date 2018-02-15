package com.sap.hana.cloud.samples.benefits.odata.cfg;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ODataContextFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} finally {
			BenefitsODataServiceFactory.unsetContextInThreadLocal();
		}
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
