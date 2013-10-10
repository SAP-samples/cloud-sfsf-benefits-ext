package com.sap.benefits.management.connectivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.sap.benefits.management.connectivity.base.ODataConnector;
import com.sap.benefits.management.connectivity.helper.CoreODataParser;
import com.sap.benefits.management.connectivity.helper.SFUser;
import com.sap.benefits.management.persistence.model.User;

@SuppressWarnings("nls")
public class CoreODataConnector extends ODataConnector {

	private static final String UTF_8 = "UTF-8";
	private static CoreODataConnector INSTANCE = null;
	private static final String MANAGED_EMPLOYEES_QUERY = "User?$select=userId,firstName,lastName,email&$filter=hr/userId%20eq%20'#'";
	private static final String PROFILE_QUERY = "User('#')";

	public static synchronized CoreODataConnector getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CoreODataConnector();
		}
		return INSTANCE;
	}

	private CoreODataConnector() {
		super("java:comp/env/sap_hcmcloud_core_odata");
	}

	private String getMangedEmployeesQuery(String hrSFUserName) throws UnsupportedEncodingException {
		return MANAGED_EMPLOYEES_QUERY.replace("#", urlEncode(hrSFUserName));
	}

	private String getProfileQuery(String userName) throws UnsupportedEncodingException {
		return PROFILE_QUERY.replace("#", urlEncode(userName));
	}

	private String urlEncode(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, UTF_8);
	}

	public List<User> getManagedEmployees(String hrSFUserName) throws IOException {
		String userListJson = getODataResponse(getMangedEmployeesQuery(hrSFUserName));
		CoreODataParser parser = CoreODataParser.getInstance();
		return createUserList(parser.loadSFUserProfileListFromJsom(userListJson));
	}

	public User getUserProfile(String userName) throws IOException {
		String userJson = getODataResponse(getProfileQuery(userName));
		CoreODataParser parser = CoreODataParser.getInstance();
		return createUser(parser.loadSFUserProfileFromJsom(userJson));
	}

	private List<User> createUserList(List<SFUser> sfUserList) {
		List<User> result = new ArrayList<>(sfUserList.size());
		for (SFUser sfUser : sfUserList) {
			result.add(createUser(sfUser));
		}
		return result;
	}

	private User createUser(SFUser sfUser) {
		User user = new User();
		user.setEmail(sfUser.email);
		user.setFirstName(sfUser.firstName);
		user.setLastName(sfUser.lastName);
		user.setUserId(sfUser.userId);
		return user;
	}

}
