package com.sap.hana.cloud.samples.benefits.odata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.core.exception.ODataRuntimeException;

import com.sap.hana.cloud.samples.benefits.commons.UserInfo;
import com.sap.hana.cloud.samples.benefits.connectivity.CoreODataConnector;
import com.sap.hana.cloud.samples.benefits.persistence.model.User;

public class UserFunctionImport extends ODataService {

	@EdmFunctionImport(name = "managed", entitySet = "Users", returnType = @ReturnType(type = Type.ENTITY, isCollection = true))
	public List<User> getManagedUsers() {
		User currentUser = getLoggedInUser();
		return currentUser.getEmployees();
	}

	@EdmFunctionImport(name = "userInfo", entitySet = "UserInfo", returnType = @ReturnType(type = Type.COMPLEX, isCollection = true))
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
		} catch (IOException e) {
			throw new ODataRuntimeException("Cannot get information about the user", e); //$NON-NLS-1$
		}

		return users;
	}

	@EdmFunctionImport(name = "profile", entitySet = "Users", returnType = @ReturnType(type = Type.ENTITY))
	public User getUserProfile() {
		User currentUser = getLoggedInUser();
		return currentUser;
	}

}
