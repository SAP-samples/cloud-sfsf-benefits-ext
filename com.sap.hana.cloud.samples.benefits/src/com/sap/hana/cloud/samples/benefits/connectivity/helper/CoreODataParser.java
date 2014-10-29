package com.sap.hana.cloud.samples.benefits.connectivity.helper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.sap.hana.cloud.samples.benefits.api.util.GsonFactory;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserPhoto;

public class CoreODataParser {

	private static final CoreODataParser INSTANCE = new CoreODataParser();

	public static CoreODataParser getInstance() {
		return INSTANCE;
	}

	private final Gson defaultGson = GsonFactory.getInstance().createDefaultGson();

	private CoreODataParser() {
	}

	@SuppressWarnings("synthetic-access")
	public SFUser loadSFUserProfileFromJsom(String json) throws IOException {
		return load(json, new JsonLoadingJob<SFUser>() {
			@Override
			public SFUser loadFromJson(JsonReader reader) {
				return defaultGson.fromJson(reader, SFUser.class);
			}

		});
	}

	@SuppressWarnings("synthetic-access")
	public UserInfo loadUserInfoFromJson(String json) throws IOException {
		return load(json, new JsonLoadingJob<UserInfo>() {
			@Override
			public UserInfo loadFromJson(JsonReader reader) {
				return defaultGson.fromJson(reader, UserInfo.class);
			}
		});
	}

	@SuppressWarnings("synthetic-access")
	public List<SFUser> loadSFUserProfileListFromJsom(String json) throws IOException {
		if (json == null) {
			return Collections.emptyList();
		}

		return load(json, new JsonLoadingJob<List<SFUser>>() {
			@Override
			public List<SFUser> loadFromJson(JsonReader reader) {
				SFUserList sfUserList = defaultGson.fromJson(reader, SFUserList.class);
				return sfUserList.results;
			}
		});
	}

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

	@SuppressWarnings("synthetic-access")
	public String loadUserPhoto(String json) throws IOException {
		return load(json, new JsonLoadingJob<String>() {
			@Override
			public String loadFromJson(JsonReader reader) {
				UserPhoto userPhoto = defaultGson.fromJson(reader, UserPhoto.class);
				return userPhoto.getPhoto();
			}
		});
	}

	private <T> T load(String json, JsonLoadingJob<T> job) throws IOException {
		JsonReader reader = createJsonReader(json);
		try {
			return job.loadFromJson(reader);
		} finally {
			closeJsonReader(reader);
		}
	}

	private interface JsonLoadingJob<T> {
		T loadFromJson(JsonReader reader);
	}
}
