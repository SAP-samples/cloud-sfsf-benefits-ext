package com.sap.hana.cloud.samples.benefits.connectivity.http;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ECAPISession {
	
	private Map<String, String> sessionHeaders;

	private static final ThreadLocal<ECAPISession> session = new ThreadLocal<ECAPISession>() {
		protected ECAPISession initialValue() {
			return new ECAPISession();
		}
	};
	
	private ECAPISession() {
		sessionHeaders = new HashMap<String, String>();
	}
	
	public static ECAPISession getInstance() {
		return session.get();
	}
	
	public void cleanUp() {
		session.remove();
	}
	
	public Map<String, String> getSessionHeaders() {
		return Collections.unmodifiableMap(sessionHeaders);
	}
	
	public void updateSessionHeaders(HttpURLConnection connection) {
		updateSessionHeader("Cookie", connection.getHeaderField("Set-Cookie"));
		updateSessionHeader("X-CSRF-Token", connection.getHeaderField("X-CSRF-Token"));
	}
	
	public void updateSessionHeaders(Map<String, String> newSessionHeaders) {
		sessionHeaders.clear();
		sessionHeaders.putAll(newSessionHeaders);
	}
	
	private void updateSessionHeader(String name, String value) {
		if (value != null) {
			sessionHeaders.put(name, value);
		}
	}
}
