package com.sap.hana.cloud.samples.benefits.connectivity.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.sap.hana.cloud.samples.benefits.api.util.GsonFactory;
import com.sap.hana.cloud.samples.benefits.odata.beans.BenefitsAmount;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserPhoto;

public class CoreODataParser {

	private static final CoreODataParser INSTANCE = new CoreODataParser();

	public static CoreODataParser getInstance() {
		return INSTANCE;
	}

	private final Gson defaultGson = GsonFactory.getInstance().createDefaultGson();
	private final Gson annotatedGson = GsonFactory.getInstance().createAnnotatedGson();

	private CoreODataParser() {
	}

	@SuppressWarnings("resource")
	public SFUser loadSFUserProfileFromJsom(String json) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			return defaultGson.fromJson(reader, SFUser.class);
		} finally {
			closeJsonReader(reader);
		}
	}

	@SuppressWarnings("resource")
	public UserInfo loadUserInfoFromJson(String json) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			return defaultGson.fromJson(reader, UserInfo.class);
		} finally {
			closeJsonReader(reader);
		}
	}

	public List<SFUser> loadSFUserProfileListFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		JsonReader reader = createJsonReader(json);
		try {
			SFUserList sfUserList = defaultGson.fromJson(reader, SFUserList.class);
			return sfUserList.results;
		} finally {
			closeJsonReader(reader);
		}
	}

	public BenefitsAmount loadUserBenefitAmount(String json) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			return defaultGson.fromJson(reader, BenefitsAmount.class);
		} finally {
			closeJsonReader(reader);
		}
	}

	@SuppressWarnings("resource")
	private JsonReader createJsonReader(String json) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(json));
		return openJsonReader(reader);
	}

	private JsonReader openJsonReader(JsonReader reader) throws IOException {
		// Bypasses the top level element
		reader.beginObject();
		reader.nextName();
		return reader;
	}

	private JsonReader closeJsonReader(JsonReader reader) throws IOException {
		reader.endObject();
		reader.close();
		return reader;
	}

	@SuppressWarnings("resource")
	public String loadUserPhoto(String json) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			UserPhoto userPhoto = defaultGson.fromJson(reader, UserPhoto.class);
			return userPhoto.getPhoto();
		} finally {
			closeJsonReader(reader);
		}
	}

	@SuppressWarnings("resource")
	public List<BenefitsAmount> loadUsersBenefitsAmount(String json) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			BenefitsAmountList result = annotatedGson.fromJson(reader, BenefitsAmountList.class);
			return result.results;
		} finally {
			closeJsonReader(reader);
		}
	}

	private class BenefitsAmountList {
		@Expose
		private List<BenefitsAmount> results;
	}
}
