package com.sap.hana.cloud.samples.benefits.connectivity.http;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.StringUtils;

@SuppressWarnings("nls")
public class DefaultHTTPResponseValidator implements HTTPResponseValidator {

    @Override
    public void validateHTTPResponse(SimpleHttpResponse httpResponse) throws InvalidResponseException {
        validateStatusCode(httpResponse);
        validateContentType(httpResponse);
    }

    private void validateStatusCode(SimpleHttpResponse httpResponse) throws InvalidResponseException {
        final int statusCode = httpResponse.getResponseCode();
        final String statusLine = httpResponse.getResponseMessage();
        if (statusCode == HttpServletResponse.SC_OK) {
            return;
        }

        String errMessage;
        switch (statusCode) {
        case HttpServletResponse.SC_NOT_FOUND:
            errMessage = String.format("Requesting path [%s] was not found.", httpResponse.getRequestPath());
            break;
        case HttpServletResponse.SC_UNAUTHORIZED:
            errMessage = String.format("Missing or incorrect credentials for path [%s].", httpResponse.getRequestPath());
            break;
        case HttpServletResponse.SC_FORBIDDEN:
            errMessage = String.format("Unauthorized request to path [%s].", httpResponse.getRequestPath());
            break;
        default:
            errMessage = String.format("Requesting path [%s] returns unexpected response.", httpResponse.getRequestPath());
        }
        errMessage += String.format(" Service returned [%d] [%s].", statusCode, statusLine);

        throw new InvalidResponseException(errMessage);
    }

    private void validateContentType(SimpleHttpResponse httpResponse) throws InvalidResponseException {
        if (StringUtils.isEmpty(httpResponse.getContentType())) {
            throw new InvalidResponseException(String.format("Response content type not found when requesting path [%s]", httpResponse.getRequestPath()));
        }
        if (!httpResponse.getContentType().contains(MediaType.APPLICATION_JSON)) {
            throw new InvalidResponseException(String.format("Invalid response content type [%s] when requesting path [%s]",
                    httpResponse.getContentType(), httpResponse.getRequestPath()));
        }
    }

}
