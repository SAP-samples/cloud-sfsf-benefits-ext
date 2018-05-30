package com.sap.hana.cloud.samples.benefits.logout;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.security.auth.login.LoginContextFactory;

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (request.getRemoteUser() != null) {
				logout();
			} else {
				response.sendRedirect("logout.jsp"); //$NON-NLS-1$
			}

		} catch (Exception e) {
			logger.error("Logout failed", e); //$NON-NLS-1$
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void logout() throws LoginException {
		LoginContext loginContext = LoginContextFactory.createLoginContext();
		loginContext.logout();
	}

}
