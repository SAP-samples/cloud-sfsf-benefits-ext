package com.sap.hana.cloud.samples.benefits.connectivity.http;

/**
 * Simple HTTP Response Wrapper.
 * @author i024072
 *
 */
public class SimpleHttpResponse {
	
    private final String requestPath;
	private final int responseCode;
	private final String responseMessage;
	private String contentType;
	private String content;
	
	public SimpleHttpResponse(String requestPath, int responseCode, String responseMessage) {
	    this.requestPath = requestPath;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

    public String getRequestPath() {
        return requestPath;
    }
	
}
