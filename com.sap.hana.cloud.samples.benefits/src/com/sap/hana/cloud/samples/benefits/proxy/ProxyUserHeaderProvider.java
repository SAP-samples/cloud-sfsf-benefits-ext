package com.sap.hana.cloud.samples.benefits.proxy;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.sap.hana.cloud.samples.benefits.odata.UserManager;

public class ProxyUserHeaderProvider {

	private static final String PROXY_USER_MAPPING_HEADER_NAME = "X-Proxy-User-Mapping"; //$NON-NLS-1$
	private static final String SF_HR_MANAGER_USER = "mbarista1"; //$NON-NLS-1$
	private static final String SF_MANAGED_EMPLOYEE_USER = "nnnn"; //$NON-NLS-1$
	private static final char USER_SEPARATOR = '|';

	public Header createMappingHeader() {
		String headerValue = UserManager.getIsUserAdmin() ? createMappingValue(SF_HR_MANAGER_USER) : createMappingValue(SF_MANAGED_EMPLOYEE_USER);
		return new BasicHeader(PROXY_USER_MAPPING_HEADER_NAME, headerValue);
	}

	private String createMappingValue(String sfUser) {
		StringBuilder valueBuilder = new StringBuilder();
		valueBuilder.append(UserManager.getUserId()).append(USER_SEPARATOR).append(sfUser);
		return valueBuilder.toString();
	}

}
