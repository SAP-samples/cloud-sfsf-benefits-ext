package com.sap.hana.cloud.samples.benefits.connectivity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.sap.hana.cloud.samples.benefits.connectivity.base.ODataConnector;
import com.sap.hana.cloud.samples.benefits.connectivity.helper.CoreODataParser;
import com.sap.hana.cloud.samples.benefits.connectivity.helper.SFUser;
import com.sap.hana.cloud.samples.benefits.odata.beans.BenefitsAmount;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;
import com.sap.hana.cloud.samples.benefits.validation.exception.InvalidResponseException;

@SuppressWarnings("nls")
public class CoreODataConnector extends ODataConnector {

	private static final String UTF_8 = "UTF-8";
	private static CoreODataConnector INSTANCE = null;
	private static final String MANAGED_EMPLOYEES_QUERY = "User?$select=userId,firstName,lastName,email&$filter=hr/userId%20eq%20'#'";
	private static final String PROFILE_QUERY = "User('#')?$select=userId,firstName,lastName,email,hr/userId,hr/firstName,hr/lastName,hr/email&$expand=hr";
	private static final String INFO_QUERY = "User('#')?$select=userId,firstName,lastName,location,businessPhone,division,title,department,email,hr/firstName,hr/lastName,hr/businessPhone&$expand=hr";
	private static final String USER_PHOTO_QUERY = "Photo(photoType=#1,userId='#2')?$select=photo";

	private CoreODataParser coreODataParser;

	// ,hr/firstName,hr/lastName,hr/email,hr/businessPhone
	public static synchronized CoreODataConnector getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CoreODataConnector();
		}
		return INSTANCE;
	}

	private CoreODataConnector() {
		super("java:comp/env/sap_hcmcloud_core_odata");
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
			return URLEncoder.encode(text, UTF_8);
		} catch (UnsupportedEncodingException e) {
			String errMsg = String.format("Fail to encode text [%s]. Unsupported encoding [%s]", text, UTF_8);
			throw new IllegalArgumentException(errMsg, e);
		}
	}

	public List<SFUser> getManagedEmployees(String hrSFUserName) throws IOException, InvalidResponseException {
		String userListJson = getODataResponse(getMangedEmployeesQuery(hrSFUserName));
		return coreODataParser.loadSFUserProfileListFromJsom(userListJson);
	}

	public SFUser getUserProfile(String userName) throws IOException, InvalidResponseException {
		String userJson = getODataResponse(getProfileQuery(userName));
		return coreODataParser.loadSFUserProfileFromJsom(userJson);
	}

	public UserInfo getUserInfoProfile(String userName) throws IOException, InvalidResponseException {
		String userJson = getODataResponse(getInfoQuery(userName));
		return coreODataParser.loadUserInfoFromJson(userJson);
	}

	public BenefitsAmount getUserBenefitsAmount(String userId) {
		return BenefitsAmount.defaultBenefitsAmount(userId);
	}

	public String getUserPhoto(String userId, Integer photoType) throws IOException, InvalidResponseException {
		String userPhotoJSON = getODataResponse(getUserPhotoQuery(userId, photoType));
		return coreODataParser.loadUserPhoto(userPhotoJSON);

	}

	private String getUserPhotoQuery(String userId, Integer photoType) {
		return USER_PHOTO_QUERY.replace("#1", String.valueOf(photoType)).replace("#2", urlEncode(userId));
	}
}
