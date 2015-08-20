package com.sap.hana.cloud.samples.benefits.connectivity;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sap.hana.cloud.samples.benefits.connectivity.helper.*;
import com.sap.hana.cloud.samples.benefits.connectivity.http.*;
import com.sap.hana.cloud.samples.benefits.odata.beans.*;

@SuppressWarnings("nls")
public class ECAPIConnector {

	private static final String ECAPI_DESTINATION_NAME = "sap_hcmcloud_core_odata";
    private static ECAPIConnector INSTANCE = null;
	private static final String MANAGED_EMPLOYEES_QUERY = "User?$select=userId,firstName,lastName,email&$filter=hr/userId%20eq%20'#'";
	private static final String PROFILE_QUERY = "User('#')?$select=userId,firstName,lastName,email,hr/userId,hr/firstName,hr/lastName,hr/email&$expand=hr";
	private static final String INFO_QUERY = "User('#')?$select=userId,firstName,lastName,location,businessPhone,division,title,department,email,hr/firstName,hr/lastName,hr/businessPhone&$expand=hr";
	private static final String USER_PHOTO_QUERY = "Photo(photoType=#1,userId='#2')?$select=photo";

	private final CoreODataParser coreODataParser;
	private final HTTPConnector httpConnector;	

	// ,hr/firstName,hr/lastName,hr/email,hr/businessPhone
	public static synchronized ECAPIConnector getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ECAPIConnector();
		}
		return INSTANCE;
	}

	private ECAPIConnector() {
	    
		this.httpConnector = new HTTPConnector(ECAPI_DESTINATION_NAME);
		this.coreODataParser = CoreODataParser.getInstance();
	}

	private String getMangedEmployeesQuery(String hrSFUserName) {
		return MANAGED_EMPLOYEES_QUERY.replace("#", urlEncode(hrSFUserName));
	}

	private String getProfileQuery(String userName) {
		return PROFILE_QUERY.replace("#", urlEncode(userName));
	}

	private String getInfoQuery(String userName) {
		return INFO_QUERY.replace("#", urlEncode(userName));
	}

	private String urlEncode(String text) {
		try {
			return URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			String errMsg = String.format("Fail to encode text [%s]. Unsupported encoding [%s]", text, StandardCharsets.UTF_8.toString());
			throw new IllegalArgumentException(errMsg, e);
		}
	}

	public List<SFUser> getManagedEmployees(String hrSFUserName) throws IOException, InvalidResponseException {
		String userListJson = executeGET(getMangedEmployeesQuery(hrSFUserName));
		return coreODataParser.loadSFUserProfileListFromJsom(userListJson);
	}

	public SFUser getUserProfile(String userName) throws IOException, InvalidResponseException {
		String userJson = executeGET(getProfileQuery(userName));
		return coreODataParser.loadSFUserProfileFromJsom(userJson);
	}

	public UserInfo getUserInfoProfile(String userName) throws IOException, InvalidResponseException {
		String userJson = executeGET(getInfoQuery(userName));
		return coreODataParser.loadUserInfoFromJson(userJson);
	}

	public BenefitsAmount getUserBenefitsAmount(String userId) {
		return BenefitsAmount.defaultBenefitsAmount(userId);
	}

	public String getUserPhoto(String userId, Integer photoType) throws IOException, InvalidResponseException {
		String userPhotoJSON = executeGET(getUserPhotoQuery(userId, photoType));
		return coreODataParser.loadUserPhoto(userPhotoJSON);
	}
	
	private String executeGET(String query) throws InvalidResponseException, IOException {
	    return this.httpConnector.executeGET(query).getContent();
	}

	private String getUserPhotoQuery(String userId, Integer photoType) {
		return USER_PHOTO_QUERY.replace("#1", String.valueOf(photoType)).replace("#2", urlEncode(userId));
	}
}
