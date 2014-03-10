package com.sap.hana.cloud.samples.benefits.logout;

import java.io.IOException;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.security.auth.login.LoginContextFactory;

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getRemoteUser() != null) {
			try {
				logout(request);
				response.sendRedirect("logout.jsp"); //$NON-NLS-1$
			} catch (LoginException e) {
				response.getWriter().println("Logout failed. Reason: " + e.getMessage()); //$NON-NLS-1$
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {			
			response.sendRedirect("logout.jsp"); //$NON-NLS-1$
		}
	}

	private void logout(HttpServletRequest request) throws LoginException {
		LoginContext loginContext = LoginContextFactory.createLoginContext();
		loginContext.logout();
	}

}
