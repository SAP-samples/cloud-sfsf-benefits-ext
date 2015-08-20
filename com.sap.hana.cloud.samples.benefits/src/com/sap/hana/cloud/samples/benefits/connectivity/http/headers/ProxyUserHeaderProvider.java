package com.sap.hana.cloud.samples.benefits.connectivity.http.headers;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;
import com.sap.hana.cloud.samples.benefits.odata.UserManager;

/**
 * Header provider that adds user mapping header, required when using mock API Endpoint.
 * @author i024072
 *
 */
@SuppressWarnings("nls")
public class ProxyUserHeaderProvider {

	private static final String PROXY_USER_MAPPING_HEADER_NAME = "X-Proxy-User-Mapping"; 
	private static final String SF_HR_MANAGER_USER = "mbarista1"; 
	private static final String SF_MANAGED_EMPLOYEE_USER = "nnnn";
	private static final char USER_SEPARATOR = '|';

	public AuthenticationHeader createMappingHeader() {
		String headerValue = UserManager.getIsUserAdmin() ? createMappingValue(SF_HR_MANAGER_USER) : createMappingValue(SF_MANAGED_EMPLOYEE_USER);
		return new AuthenticationHeaderImpl(PROXY_USER_MAPPING_HEADER_NAME, headerValue);
	}

	private String createMappingValue(String sfUser) {
		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append(UserManager.getUserId()).append(USER_SEPARATOR).append(sfUser);
		return valueBuilder.toString();
	}

}
