package com.sap.hana.cloud.samples.benefits.odata;

import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.CAMPAIGN_ID;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.PHOTO_TYPE;
import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.USER_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.HttpMethod;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;
import org.apache.olingo.odata2.core.exception.ODataRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.connectivity.CoreODataConnector;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;
import com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportEntitySets;
import com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames;
import com.sap.hana.cloud.samples.benefits.persistence.UserPointsDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;
import com.sap.hana.cloud.samples.benefits.persistence.model.UserPoints;

public class UserService extends ODataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@EdmFunctionImport(name = FunctionImportNames.MANAGED_USER_NAMES, entitySet = FunctionImportEntitySets.MANAGED_USER_NAMES, returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<User> getManagedUsers() {
		User currentUser = getLoggedInUser();
		return currentUser.getEmployees();
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_INFO, entitySet = FunctionImportNames.USER_INFO, returnType = @ReturnType(type = Type.COMPLEX, isCollection = true))
	public List<UserInfo> getInfoProfile() {
		User currentUser = getLoggedInUser();
		UserInfo userInfo = new UserInfo();
		UserInfo hrInfo = new UserInfo();
		List<UserInfo> users = new ArrayList<>();

		try {
			userInfo = CoreODataConnector.getInstance().getUserInfoProfile(currentUser.getUserId());
			users.add(userInfo);
			if (currentUser.getHrManager() != null) {
				hrInfo = CoreODataConnector.getInstance().getUserInfoProfile(currentUser.getHrManager().getUserId());
				users.add(hrInfo);
			}
		} catch (IOException ex) {
			String errMsg = "Cannot get information about the user"; //$NON-NLS-1$
			LOGGER.error(errMsg, ex);
			throw new ODataRuntimeException(errMsg, ex);
		}

		return users;
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_PROFILE, entitySet = FunctionImportEntitySets.USER_PROFILE, returnType = @ReturnType(type = Type.ENTITY))
	public User getUserProfile() {
		User currentUser = getLoggedInUser();
		return currentUser;
	}

	@EdmFunctionImport(name = FunctionImportNames.USER_PHOTO, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.GET)
	public String getUserPhoto(@EdmFunctionImportParameter(name = PHOTO_TYPE, type = EdmType.INT32) Integer photoType) {
		return getUserPhoto(getLoggedInUser().getUserId(), photoType);
	}
	
	@EdmFunctionImport(name = FunctionImportNames.HR_PHOTO, returnType = @ReturnType(type = Type.SIMPLE, isCollection = false), httpMethod = HttpMethod.GET)
    public String getHrManagerPhoto(@EdmFunctionImportParameter(name = PHOTO_TYPE, type = EdmType.INT32) Integer photoType) {
        User hrManager = getLoggedInUser().getHrManager();
        if (hrManager == null){
            return "";
        }
        return getUserPhoto(hrManager.getUserId(), photoType);
    }

	@EdmFunctionImport(name = FunctionImportNames.USER_POINTS, entitySet = FunctionImportEntitySets.USER_POINTS, returnType = @ReturnType(type = Type.ENTITY), httpMethod = HttpMethod.GET)
	public UserPoints getCampaignUserPoints(@EdmFunctionImportParameter(name = CAMPAIGN_ID, type = EdmType.INT64) Long campaignId,
			@EdmFunctionImportParameter(name = USER_ID, type = EdmType.STRING) String userId) {
		User currentUser = getLoggedInUser();
		if (UserManager.getIsUserAdmin() || currentUser.getUserId().equals(userId)) {
			return new UserPointsDAO().getUserPoints(userId, campaignId);
		}
		throw new IllegalArgumentException("Missing user points for campaign wit id " + campaignId);
	}
	
	private String getUserPhoto(String userId, int photoType){
	    try {
            return CoreODataConnector.getInstance().getUserPhoto(userId, photoType);
        } catch (IOException ex) {
            String errMsg = String.format("Failed to get photo for user with id %s", userId); //$NON-NLS-1$
            LOGGER.error(errMsg, ex);
            throw new ODataRuntimeException(errMsg, ex);
        } 
	}
}
