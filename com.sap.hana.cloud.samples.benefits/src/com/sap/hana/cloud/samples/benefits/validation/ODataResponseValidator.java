package com.sap.hana.cloud.samples.benefits.validation;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.sap.hana.cloud.samples.benefits.validation.exception.InvalidResponseBodyException;
import com.sap.hana.cloud.samples.benefits.validation.exception.InvalidResponseCodeException;
import com.sap.hana.cloud.samples.benefits.validation.exception.InvalidResponseException;

public class ODataResponseValidator {

	public void validate(HttpResponse response, String requestPath) throws InvalidResponseException {
		validateStatusCode(response.getStatusLine().getStatusCode(), requestPath);
		validateResponseBody(response.getEntity(), requestPath);
	}

	private void validateStatusCode(int statusCode, String requestPath) throws InvalidResponseCodeException {
		if (statusCode == HttpServletResponse.SC_OK) {
			return;
		}

		String errMessage;

		switch (statusCode) {
		case 404:
			errMessage = String
					.format("Request with path [%s] not found in the SFSF OData. OData service status code [%d].", requestPath, statusCode); //$NON-NLS-1$
			break;
		case 401:
			errMessage = String.format(
					"Missing or incorrect credentials for SFSF OData request to path [%s]. OData service status code [%d].", requestPath, statusCode); //$NON-NLS-1$			
			break;
		case 403:
			errMessage = String.format("Unauthorized request to path [%s]. OData service status code [%d].", requestPath, statusCode); //$NON-NLS-1$
			break;
		default:
			errMessage = String.format("Invalid SFSF OData response status code [%d] from request to path [%s]", statusCode, //$NON-NLS-1$
					requestPath);
		}

		throw new InvalidResponseCodeException(errMessage);
	}

	private void validateResponseBody(HttpEntity entity, String requestPath) throws InvalidResponseBodyException {
		if ((entity != null) && (entity.getContentType() != null)) {
			if (!entity.getContentType().getValue().contains(MediaType.APPLICATION_JSON)) {
				throw new InvalidResponseBodyException(String.format("Invalid response content type [%s] from the SFSF OData request to path [%s]", //$NON-NLS-1$
						entity.getContentType().getValue(), requestPath));
			}
		}

	}

}
