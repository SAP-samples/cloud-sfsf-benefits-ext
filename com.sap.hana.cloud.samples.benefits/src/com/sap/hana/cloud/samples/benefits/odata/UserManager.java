package com.sap.hana.cloud.samples.benefits.odata;

public class UserManager {

	private static final ThreadLocal<String> userId = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> isUserAdmin = new ThreadLocal<>();

	public static void setUserId(String data) {
		userId.set(data);
	}

	public static String getUserId() {
		return userId.get();
	}

	public static void setIsUserAdmin(Boolean data) {
		isUserAdmin.set(data);
	}

	public static Boolean getIsUserAdmin() {
		return isUserAdmin.get();
	}

	public static void cleanUp() {
		userId.remove();
		isUserAdmin.remove();
	}

}
