package com.sap.hana.cloud.samples.benefits.api.util;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonFactory {
	
	private static final GsonFactory INSTANCE = new GsonFactory();
	
	public static GsonFactory getInstance() {
		return INSTANCE;
	}
	
	private final Gson gson;
	
	private GsonFactory() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateTimeAdapter());
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		this.gson = gsonBuilder.create();
	}
	
	public Gson getGson() {
		return this.gson;
	}

}
