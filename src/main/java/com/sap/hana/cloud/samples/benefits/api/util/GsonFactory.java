package com.sap.hana.cloud.samples.benefits.api.util;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonFactory {

	private static final GsonFactory INSTANCE = new GsonFactory();

	public static GsonFactory getInstance() {
		return INSTANCE;
	}

	private GsonFactory() {
	}

	public Gson createDefaultGson() {
		return createaDefaultJsonBuilder().create();
	}

	public Gson createAnnotatedGson() {
		GsonBuilder gb = createaDefaultJsonBuilder();
		return gb.excludeFieldsWithoutExposeAnnotation().create();
	}

	private GsonBuilder createaDefaultJsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateTimeAdapter());
		gsonBuilder.setPrettyPrinting();
		return gsonBuilder;
	}

}
