package com.sap.hana.cloud.samples.benefits.connectivity.base;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.http.HttpDestination;
import com.sap.hana.cloud.samples.benefits.proxy.ProxyUserHeaderProvider;
import com.sap.hana.cloud.samples.benefits.validation.ODataResponseValidator;
import com.sap.hana.cloud.samples.benefits.validation.exception.InvalidResponseException;

public abstract class ODataConnector {
	private static final Header ACCEPT_JSON_HEADER = new BasicHeader("Accept", MediaType.APPLICATION_JSON); //$NON-NLS-1$

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ProxyUserHeaderProvider proxyUserHeaderProvider = new ProxyUserHeaderProvider();
	private final String destinationPath;

	private ODataResponseValidator responseValidator;

	public ODataConnector(String destinationPath) {
		this.destinationPath = destinationPath;
		this.responseValidator = new ODataResponseValidator();
	}

	public String getODataResponse(String path) throws InvalidResponseException, IOException {
		HttpClient client = null;

		try {
			client = getHTTPClient();
			return getODataResponse(path, client);
		} finally {
			releaseClient(client);
		}
	}

	private String getODataResponse(String path, HttpClient httpClient) throws InvalidResponseException, IOException {
		HttpGet get = createGetRequest(path);
		HttpResponse response;
		try {
			response = httpClient.execute(get);
		} catch (IOException ex) {
			throw new IOException(String.format("Fail to execute request to path [%s]. Reason: [%s]", path, ex.getMessage())); //$NON-NLS-1$
		}
		logResponseCode(response);
		responseValidator.validate(response, path);

		return getJsonResponse(response, path);
	}

	private HttpGet createGetRequest(String path) {
		HttpGet get = new HttpGet(path);
		logDebugMessage("HTTP Client for OData destination requesting relative path [" + path + "]"); //$NON-NLS-1$ //$NON-NLS-2$ 
		addGetMethodHeaders(get);
		return get;
	}

	private void addGetMethodHeaders(HttpGet get) {
		get.addHeader(ACCEPT_JSON_HEADER);
		get.addHeader(proxyUserHeaderProvider.createMappingHeader());
	}

	private void logResponseCode(HttpResponse response) {
		logDebugMessage("Destination [" + destinationPath + "] request returned " + response.getStatusLine().getStatusCode() + " " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ response.getStatusLine().getReasonPhrase());
		HttpEntity entity = response.getEntity();
		if ((entity != null) && (entity.getContentType() != null)) {
			logDebugMessage("Destination [" + destinationPath + "] request returned content [" + entity.getContentType().getValue() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private String getJsonResponse(HttpResponse response, String path) throws IOException {
		String result;
		try {
			result = EntityUtils.toString(response.getEntity());
		} catch (ParseException | IOException ex) {
			throw new IOException(String.format("Fail to get response content from SFSF OData request to path [%s]. Reason: [%s]", path, //$NON-NLS-1$
					ex.getMessage()));
		}
		logDebugMessage("Query [" + path + "] response: " + result); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}

	private HttpClient getHTTPClient() throws IOException {
		HttpDestination destination = lookupDestination();
		logDebugMessage("Lookup of OData destination [" + destinationPath + "] success."); //$NON-NLS-1$ //$NON-NLS-2$
		HttpClient client;
		try {
			client = destination.createHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 40000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 45000);
		} catch (DestinationException e) {
			throw new RuntimeException(e);
		}
		try {
			logDebugMessage("HTTP Client for OData destination [" + destinationPath + "] created using base path [" + destination.getURI().toString() //$NON-NLS-1$ //$NON-NLS-2$
					+ "]"); //$NON-NLS-1$
		} catch (URISyntaxException e) {
			logger.error("Invalid URI specified for destination [" + destinationPath + "].", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return client;
	}

	private HttpDestination lookupDestination() throws IOException {
		try {
			Context ctx = new InitialContext();
			return (HttpDestination) ctx.lookup(destinationPath);
		} catch (NamingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private void releaseClient(HttpClient client) {
		if (client == null) {
			return;
		}
		ClientConnectionManager connectionManager = client.getConnectionManager();
		if (connectionManager != null) {
			connectionManager.shutdown();
		}
	}

	private void logDebugMessage(String debugMessage) {
		if (logger.isDebugEnabled()) {
			logger.debug(debugMessage);
		}
	}

}
