package com.sap.hana.cloud.samples.benefits.connectivity.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;
import com.sap.core.connectivity.api.authentication.AuthenticationHeaderProvider;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.sap.hana.cloud.samples.benefits.connectivity.http.headers.BasicAuthenticationHeaderProvider;
import com.sap.hana.cloud.samples.benefits.connectivity.http.headers.ProxyUserHeaderProvider;

@SuppressWarnings("nls")
public class HTTPConnector {

    private static final String PATH_SUFFIX = "/";
    private static final String ACCEPT_HEADER = "Accept";
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String DESTINATION_URL = "URL";

    private static final Logger logger = LoggerFactory.getLogger(HTTPConnector.class);

    private final ProxyUserHeaderProvider proxyUserHeaderProvider = new ProxyUserHeaderProvider();
    private final String destinationName;
    private AuthenticationHeaderProvider localAuthenticationHeaderProvider;
    private ConnectivityConfiguration localConnectivityConfiguration;

    private HTTPResponseValidator responseValidator;

    public HTTPConnector(String destinationName) {
        this.destinationName = destinationName;
        this.responseValidator = new DefaultHTTPResponseValidator();
    }

    public SimpleHttpResponse executeGET(String path) throws InvalidResponseException, IOException {
        HttpURLConnection urlConnection = getConnection(path);
        urlConnection.addRequestProperty(ACCEPT_HEADER, MediaType.APPLICATION_JSON);
        SimpleHttpResponse response = executeGET(urlConnection);
        ECAPISession.getInstance().updateSessionHeaders(urlConnection);
        return response;
    }

    public SimpleHttpResponse executePOST(String path, String data, String contentType) throws IOException, InvalidResponseException {
    		HttpURLConnection urlConnection = getConnection(path);
    		SimpleHttpResponse response = executePOST(urlConnection, data, contentType);
    		ECAPISession.getInstance().updateSessionHeaders(urlConnection);
        return response;
    }

    private SimpleHttpResponse executePOST(HttpURLConnection connection, String data, String contentType) throws IOException, InvalidResponseException {        
        connection.setRequestMethod(POST_METHOD);
        
        if (!StringUtils.isEmpty(contentType)) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty( "charset", StandardCharsets.UTF_8.toString());
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8.toString());
            connection.setRequestProperty("Content-Length", dataBytes.toString());
            OutputStream output = connection.getOutputStream();
            output.write(dataBytes);
            output.close();
        }
        int responseCode = connection.getResponseCode();
        SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
        httpResponse.setContentType(connection.getContentType());
        httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
        logResponse(httpResponse);
        validateResponse(httpResponse);
        return httpResponse;
    }

    private SimpleHttpResponse executeGET(HttpURLConnection connection) throws IOException, InvalidResponseException {
        connection.setRequestMethod(GET_METHOD);
        int responseCode = connection.getResponseCode();
        SimpleHttpResponse httpResponse = new SimpleHttpResponse(connection.getURL().toString(), responseCode, connection.getResponseMessage());
        httpResponse.setContentType(connection.getContentType());
        httpResponse.setContent(IOUtils.toString(connection.getInputStream()));
        logResponse(httpResponse);
        validateResponse(httpResponse);
        return httpResponse;
    }

    private void logResponse(SimpleHttpResponse httpResponse) {
        logger.debug("Response from requesting {} is {} {}", httpResponse.getRequestPath(), httpResponse.getResponseCode(),
                httpResponse.getResponseMessage());
        logger.debug("Response content type from requesting {} is {}", httpResponse.getRequestPath(), httpResponse.getContentType());
        logger.debug("Response content from requesting {} is {}", httpResponse.getRequestPath(), httpResponse.getContent());
    }

    private void validateResponse(SimpleHttpResponse httpResponse) throws InvalidResponseException {
        if (this.responseValidator != null) {
            this.responseValidator.validateHTTPResponse(httpResponse);
        }
    }

    private void injectAuthenticationHeaders(HttpURLConnection urlConnection, DestinationConfiguration destinationConfiguration) throws IOException {
        List<AuthenticationHeader> authenticationHeaders = getAuthenticationHeaders(destinationConfiguration);
        authenticationHeaders.add(this.proxyUserHeaderProvider.createMappingHeader()); // User Mapping Header required for Mock API Endpoint
        for (AuthenticationHeader authenticationHeader : authenticationHeaders) {
            urlConnection.addRequestProperty(authenticationHeader.getName(), authenticationHeader.getValue());
        }
    }

    private URL getRequestURL(DestinationConfiguration destinationConfiguration, String path) throws IOException {
        String requestBaseURL = destinationConfiguration.getProperty(DESTINATION_URL);
        if (StringUtils.isEmpty(requestBaseURL)) {
            String errorMessage = String.format("Request URL in Destination %s is not configured. Make sure to have the destination configured.",
                    this.destinationName);
            throwConfigurationError(errorMessage);
        }
        if (!requestBaseURL.endsWith(PATH_SUFFIX)) {
            requestBaseURL = requestBaseURL + PATH_SUFFIX;
        }
        URL baseURL = new URL(requestBaseURL);
        URL fullURL = new URL(baseURL, path);
        logger.info("HTTP Request from destination {} with base URL {} and relative path {} resolved to {}", this.destinationName, requestBaseURL, path, fullURL.toString());
        return fullURL;
    }

    private void throwConfigurationError(String errorMessage) throws IOException {
        logger.error(errorMessage);
        throw new IOException(errorMessage);
    }

    private synchronized AuthenticationHeaderProvider lookupAuthenticationHeaderProvider() throws IOException {
        try {
            if (this.localAuthenticationHeaderProvider == null) {
                Context ctx = new InitialContext();
                this.localAuthenticationHeaderProvider = (AuthenticationHeaderProvider) ctx.lookup("java:comp/env/authenticationHeaderProvider");
            }
            return this.localAuthenticationHeaderProvider;
        } catch (NamingException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private List<AuthenticationHeader> getAuthenticationHeaders(DestinationConfiguration destinationConfiguration) throws IOException {
        String authenticationType = destinationConfiguration.getProperty("Authentication");
        List<AuthenticationHeader> authenticationHeaders = new ArrayList<>();
        if ("OAuth2SAMLBearerAssertion".equals(authenticationType)) {
            AuthenticationHeaderProvider headerProvider = lookupAuthenticationHeaderProvider();
            authenticationHeaders.addAll(headerProvider.getOAuth2SAMLBearerAssertionHeaders(destinationConfiguration));
        } else if ("BasicAuthentication".equals(authenticationType)) {
            BasicAuthenticationHeaderProvider headerProvider = new BasicAuthenticationHeaderProvider();
            authenticationHeaders.add(headerProvider.getAuthenticationHeader(destinationConfiguration));
        }
        return authenticationHeaders;
    }

    private DestinationConfiguration lookupDestinationConfiguration() throws IOException {
        ConnectivityConfiguration configuration = lookupConnectivityConfiguration();

        DestinationConfiguration destinationConfiguration = configuration.getConfiguration(this.destinationName);
        if (destinationConfiguration == null) {
            String errorMessage = String.format("Destination %s is not found. Make sure to have the destination configured.", this.destinationName);
            logger.error(errorMessage);
            throw new IOException(errorMessage);
        }
        return destinationConfiguration;
    }

    private synchronized ConnectivityConfiguration lookupConnectivityConfiguration() throws IOException {
        try {
            if (this.localConnectivityConfiguration == null) {
                Context ctx = new InitialContext();
                this.localConnectivityConfiguration = (ConnectivityConfiguration) ctx.lookup("java:comp/env/connectivityConfiguration");
            }
            return this.localConnectivityConfiguration;
        } catch (NamingException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
    
    private HttpURLConnection getConnection(String path) throws IOException {
        DestinationConfiguration destinationConfiguration = lookupDestinationConfiguration();
        URL requestURL = getRequestURL(destinationConfiguration, path);
        HttpURLConnection urlConnection = (HttpURLConnection) requestURL.openConnection();
        injectAuthenticationHeaders(urlConnection, destinationConfiguration);
        Map<String, String> sessionHeaders = ECAPISession.getInstance().getSessionHeaders();
        for (Entry<String, String> sessionHeader : sessionHeaders.entrySet()) {
        		urlConnection.setRequestProperty(sessionHeader.getKey(), sessionHeader.getValue());
        }
        return urlConnection;
    }

}
