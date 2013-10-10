package com.sap.benefits.management.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.benefits.management.connectivity.CoreODataConnector;
import com.sap.benefits.management.connectivity.helper.SFUser;
import com.sap.benefits.management.persistence.UserDAO;
import com.sap.benefits.management.persistence.model.User;

@SuppressWarnings("nls")
public class SessionCreateFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String loggedInUser = httpRequest.getRemoteUser();
		if (loggedInUser != null) {
			initUserSession(loggedInUser, httpRequest);
		}
		filterChain.doFilter(request, response);
	}

	private void initUserSession(String loggedInUser, HttpServletRequest request) {
		Object userLock = UserLock.getInstance().getUserLock(loggedInUser);
		synchronized (userLock) { // Lock based on user prevents from concurrent
									// // user session initialization
			String initialFlag = (String) request.getSession().getAttribute(SessionListener.INITIAL_FLAG);
			if (initialFlag != null) {
				logger.info("User '{}' session is initialized.", loggedInUser);
				UserDAO userDAO = getUserDAO();
				User user = initSingleUserProfile(loggedInUser, userDAO);
				if (request.isUserInRole("Administrator") && user != null) {
					initManagedUsers(user, userDAO);
				}
				request.getSession().removeAttribute(SessionListener.INITIAL_FLAG);
			}
		}
	}

	private void initManagedUsers(User hrUser, UserDAO userDAO) {
		try {
			List<SFUser> managedSFUsers = CoreODataConnector.getInstance().getManagedEmployees(hrUser.getUserId());
			// Check if users exists in database and create their profiles
			for (SFUser managedSFUser : managedSFUsers) {
				User appUser = userDAO.getByUserId(managedSFUser.userId);
				if (appUser == null) { // Create new user profile
					appUser = new User();
					managedSFUser.write(appUser);
					appUser.setHrManager(hrUser);
					userDAO.saveNew(appUser);
				}/* else {
					managedSFUser.write(appUser);
					appUser.setHrManager(hrUser);
					userDAO.save(appUser);
				}*/
			}
		} catch (IOException e) {
			logger.error("User '{}' managed users could not be obtained from Success Factors.", hrUser.getUserId(), e);
		}

	}

	private UserDAO getUserDAO() {
		return new UserDAO();
	}

	private User initSingleUserProfile(String userName, UserDAO userDAO) {
		User user = userDAO.getOrCreateUser(userName);

		SFUser sfUser;
		try {
			sfUser = CoreODataConnector.getInstance().getUserProfile(userName);
			sfUser.write(user);
			if (sfUser.hr != null) {
				User hrManager = userDAO.getOrCreateUser(sfUser.hr.userId);
				sfUser.hr.write(hrManager);
				hrManager = userDAO.save(hrManager);
				user.setHrManager(hrManager);
			}
			userDAO.save(user);
			logger.info("User '{}' updated in database.", userName);
		} catch (IOException e) {
			logger.info("User '{}' could not be extracted from backend.", userName, e);
		}
		return user;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
