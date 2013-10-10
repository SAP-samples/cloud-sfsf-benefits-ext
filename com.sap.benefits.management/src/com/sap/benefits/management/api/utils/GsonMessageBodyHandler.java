package com.sap.benefits.management.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SuppressWarnings("nls")
public class GsonMessageBodyHandler<T> implements MessageBodyWriter<T>, MessageBodyReader<T> {

	private final Logger logger = LoggerFactory.getLogger(GsonMessageBodyHandler.class);

	public GsonMessageBodyHandler() {
	}

	@Override
	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException, WebApplicationException {

		Reader entityReader = new InputStreamReader(entityStream, "UTF-8");
		Type targetType;
		if (Collection.class.isAssignableFrom(type)) {
			targetType = genericType;
		} else {
			targetType = type;
		}

		Gson gson = GsonFactory.getInstance().getGson();
		return gson.fromJson(entityReader, targetType);
	}

	@Override
	public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {

		try {
			if (!String.class.isAssignableFrom(type)) {
				Gson gson = GsonFactory.getInstance().getGson();
				entityStream.write(gson.toJson(t).getBytes("UTF-8"));
			} else {
				// Do not convert strings.
				entityStream.write(((String) t).getBytes("UTF-8"));
			}
		} catch (IllegalStateException e) {
			// Stream is invalidated.
			logger.info("Could not write GSON result: {}.", e.getMessage());
		}
	}

}
