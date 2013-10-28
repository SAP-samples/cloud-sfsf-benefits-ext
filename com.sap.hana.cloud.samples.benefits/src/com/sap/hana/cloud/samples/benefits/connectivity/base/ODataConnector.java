package com.sap.hana.cloud.samples.benefits.connectivity.base;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.http.HttpDestination;

/**
 * Base class which handles OData http destination connectivity and returns response as JSON.
 * 
 * @author Chavdar Baikov
 * 
 */
@SuppressWarnings("nls")
public abstract class ODataConnector {

    private static final String APPLICATION_JSON = "application/json";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String destinationPath;

    public ODataConnector(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    protected String getODataResponse(String path) throws IOException {
        HttpClient client = null;

        try {
            client = getHTTPClient();
            return getODataResponse(path, client);
        } finally {
            releaseClient(client);
        }
    }

    protected String getODataResponse(String path, HttpClient httpClient) throws IOException {
        try {
            HttpGet get = new HttpGet(path);
            logDebugMessage("HTTP Client for OData destination [" + destinationPath + "] requesting relative path [" + path + "]");
            get.setHeader("accept", APPLICATION_JSON);
            HttpResponse response = httpClient.execute(get);
            logResponseCode(response);
            return getJsonResponse(response, path);
        } catch (RuntimeException x) {
            throw new IOException(x.getMessage(), x);
        }
    }

    private void logResponseCode(HttpResponse response) {
        logDebugMessage("Destination [" + destinationPath + "] request returned " + response.getStatusLine().getStatusCode() + " "
                + response.getStatusLine().getReasonPhrase());
        HttpEntity entity = response.getEntity();
        if ((entity != null) && (entity.getContentType() != null)) {
            logDebugMessage("Destination [" + destinationPath + "] request returned content [" + entity.getContentType().getValue() + "]");
        }
    }

    private String getJsonResponse(HttpResponse response, String fullURL) throws IOException {
        if (isResponseOK(response)) {
            String result = EntityUtils.toString(response.getEntity());
            logDebugMessage("Query [" + fullURL + "] response: " + result);
            return result;
        } else if (isResponseEmpty(response)) {
            return null;
        } else {
            String statusMessage = response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase();
            if (response.getEntity() != null && response.getEntity().getContentType() != null) {
                statusMessage += " (" + response.getEntity().getContentType().toString() + ")";
            }
            throw new IOException(statusMessage);
        }
    }

    private boolean isResponseEmpty(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() == 204) {
            return true; // No Content
        }
        return false;
    }

    private boolean isResponseOK(HttpResponse response) {
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            if ((entity != null) && (entity.getContentType() != null)) {
                if (entity.getContentType().getValue().contains(getAcceptedContentType())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String getAcceptedContentType() {
        return APPLICATION_JSON;
    }

    protected HttpClient getHTTPClient() throws IOException {
        HttpDestination destination = lookupDestination();
        logDebugMessage("Lookup of OData destination [" + destinationPath + "] success.");
        HttpClient client;
        try {
            client = destination.createHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
        } catch (DestinationException e) {
            throw new RuntimeException(e);
        }
        try {
            logDebugMessage("HTTP Client for OData destination [" + destinationPath + "] created using base path [" + destination.getURI().toString() //$NON-NLS-1$ //$NON-NLS-2$
                    + "]");
        } catch (URISyntaxException e) {
            logger.error("Invalid URI specified for destination [" + destinationPath + "].", e);
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

    protected void releaseClient(HttpClient client) {
        if (client == null) {
            return;
        }
        ClientConnectionManager connectionManager = client.getConnectionManager();
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
    }

    protected void logDebugMessage(String debugMessage) {
        if (logger.isDebugEnabled()) {
            logger.debug(debugMessage);
        }
    }

}
