package com.sap.hana.cloud.samples.benefits.odata;

import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.CAMPAIGN_ID;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.PHOTO_TYPE;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.USER_ID;

import java.io.IOException;
import java.util.*;

import org.apache.olingo.odata2.api.annotation.edm.*;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.HttpMethod;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;

import com.sap.hana.cloud.samples.benefits.connectivity.ECAPIConnector;
import com.sap.hana.cloud.samples.benefits.connectivity.http.InvalidResponseException;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;
import com.sap.hana.cloud.samples.benefits.odata.cfg.*;
import com.sap.hana.cloud.samples.benefits.persistence.UserPointsDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.*;

public class UserService extends ODataService {

	@EdmFunctionImport(name = FunctionImportNames.MANAGED_USER_NAMES, entitySet = FunctionImportEntitySets.MANAGED_USER_NAMES, returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<User> getManagedUsers() {
		User currentUser = getLoggedInSfUser();
		return currentUser.getEmployees();
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_INFO, entitySet = FunctionImportNames.USER_INFO, returnType = @ReturnType(type = Type.COMPLEX, isCollection = true))
	public List<UserInfo> getInfoProfile() throws AppODataException {
		User currentUser = getLoggedInSfUser();
		UserInfo userInfo = new UserInfo();
		UserInfo hrInfo = new UserInfo();
		List<UserInfo> users = new ArrayList<>();

		try {
			userInfo = ECAPIConnector.getInstance().getUserInfoProfile(currentUser.getUserId());
			users.add(userInfo);
			if (currentUser.getHrManager() != null) {
				hrInfo = ECAPIConnector.getInstance().getUserInfoProfile(currentUser.getHrManager().getUserId());
				users.add(hrInfo);
			}
		} catch (IOException | InvalidResponseException ex) {
			throw new AppODataException("Cannot get information about the user", ex); //$NON-NLS-1$
		}

		return users;
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_PROFILE, entitySet = FunctionImportEntitySets.USER_PROFILE, returnType = @ReturnType(type = Type.ENTITY))
	public User getUserProfile() {
		User currentUser = getLoggedInSfUser();
		return currentUser;
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_PHOTO, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.GET)
	public String getUserPhoto(@EdmFunctionImportParameter(name = PHOTO_TYPE, type = EdmType.INT32) Integer photoType) throws AppODataException {
		return getUserPhoto(getLoggedInSfUser().getUserId(), photoType);
	}

	@EdmFunctionImport(name = FunctionImportNames.HR_PHOTO, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.GET)
	public String getHrManagerPhoto(@EdmFunctionImportParameter(name = PHOTO_TYPE, type = EdmType.INT32) Integer photoType) throws AppODataException {
		User hrManager = getLoggedInSfUser().getHrManager();
		if (hrManager == null) {
			return ""; //$NON-NLS-1$
		}
		return getUserPhoto(hrManager.getUserId(), photoType);
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_POINTS, entitySet = FunctionImportEntitySets.USER_POINTS, returnType = @ReturnType(type = Type.ENTITY), httpMethod = HttpMethod.GET)
	public UserPoints getCampaignUserPoints(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = EdmType.INT64) Long campaignId,
			@EdmFunctionImportParameter(name = USER_ID, type = EdmType.STRING) String userId) {
		User currentUser = getLoggedInSfUser();
		if (UserManager.getIsUserAdmin() || currentUser.getUserId().equals(userId)) {
			return new UserPointsDAO().getUserPoints(userId, campaignId);
		}
		throw new IllegalArgumentException("Missing user points for campaign wit id " + campaignId); //$NON-NLS-1$
	}

	private String getUserPhoto(String userId, int photoType) throws AppODataException {
		try {
			return ECAPIConnector.getInstance().getUserPhoto(userId, photoType);
		} catch (IOException | InvalidResponseException ex) {
			throw new AppODataException(String.format("Failed to get photo for user with id %s", userId), ex); //$NON-NLS-1$
		}
	}
}
