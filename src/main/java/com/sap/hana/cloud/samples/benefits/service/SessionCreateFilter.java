package com.sap.hana.cloud.samples.benefits.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

import com.sap.hana.cloud.samples.benefits.connectivity.ECAPIConnector;
import com.sap.hana.cloud.samples.benefits.connectivity.helper.SFUser;
import com.sap.hana.cloud.samples.benefits.connectivity.http.InvalidResponseException;
import com.sap.hana.cloud.samples.benefits.persistence.UserDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

@SuppressWarnings("nls")
public class SessionCreateFilter implements Filter {

	public static final String SF_USER_ID_ATTR_NAME = "sfUserId";
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
				User user = initSingleUserProfile(loggedInUser, userDAO, request.getSession());
				if (request.isUserInRole(ApplicationRoles.ADMINISTRATOR_ROLE) && user != null) {
					initManagedUsers(user, userDAO);
				}
				request.getSession().removeAttribute(SessionListener.INITIAL_FLAG);
			}
		}
	}

	private void initManagedUsers(User hrUser, UserDAO userDAO) {
		try {
			List<SFUser> managedSFUsers = ECAPIConnector.getInstance().getManagedEmployees(hrUser.getUserId());
			// Check if users exists in database and create their profiles if it
			// does not
			for (SFUser managedSFUser : managedSFUsers) {
				User appUser = userDAO.getByUserId(managedSFUser.userId);
				if (appUser == null) { // Create new user profile
					appUser = new User();
					managedSFUser.write(appUser);
					appUser.setHrManager(hrUser);
					userDAO.saveNew(appUser);
				} else {
					managedSFUser.write(appUser);
					appUser.setHrManager(hrUser);
					userDAO.save(appUser);
				}
			}
		} catch (IOException | InvalidResponseException ex) {
			logger.error("User '{}' managed users could not be obtained from Success Factors.", hrUser.getUserId(), ex);
		}

	}

	private UserDAO getUserDAO() {
		return new UserDAO();
	}

	private User initSingleUserProfile(String userName, UserDAO userDAO, HttpSession session) {
		try {
			SFUser sfUser = ECAPIConnector.getInstance().getUserProfile(userName);
			session.setAttribute(SF_USER_ID_ATTR_NAME, sfUser.userId);

			User user = userDAO.getByUserId(sfUser.userId);
			if (user == null) {
				user = createNewUser(sfUser, userDAO);
			}

			boolean userHasHR = sfUser.hr != null;
			if (userHasHR) {
				User hrManager = userDAO.getByUserId(sfUser.hr.userId);
				if (hrManager == null) {
					hrManager = createNewUser(sfUser.hr, userDAO);
				}
				user.setHrManager(hrManager);
			}
			userDAO.save(user);
			logger.info("User '{}' updated in database.", userName);

			return user;
		} catch (IOException | InvalidResponseException ex) {
			logger.error("User '{}' could not be extracted from backend. The user will be initialized simply.", userName, ex);			
			return createUser(userName, userDAO);
		}
	}

	private User createNewUser(SFUser sourceSfUser, UserDAO userDAO) {
		User newUser = new User();
		sourceSfUser.write(newUser);
		userDAO.saveNew(newUser);
		return newUser;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	private User createUser(String userName, UserDAO userDAO) {
		User user = userDAO.getByUserId(userName);
		if (user == null) {
			User newUser = new User(userName);
			userDAO.saveNew(newUser);
			return newUser;
		} else {
			return user;
		}
	}

}
