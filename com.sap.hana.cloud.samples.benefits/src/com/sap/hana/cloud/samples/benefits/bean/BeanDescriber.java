package com.sap.hana.cloud.samples.benefits.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDescriber {

	private Object beanObject;

	public BeanDescriber(Object beanObject) {
		this.beanObject = beanObject;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> describe() throws SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			IntrospectionException {
		List<String> propertyNames = getPropertyNames();
		Map<String, Object> props = obtainProperties(propertyNames);
		return props;
	}

	public List<String> getPropertyNames() throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(beanObject.getClass(), beanObject.getClass().getSuperclass());

		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		List<String> names = new ArrayList<>();
		for (PropertyDescriptor propDescriptor : descriptors) {
			names.add(propDescriptor.getName());
		}
		return names;
	}

	private Map<String, Object> obtainProperties(List<String> propertyNames) throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		Map<String, Object> props = new HashMap<>();
		for (String propertyName : propertyNames) {
			Object propValue = getPropertyValue(propertyName);
			props.put(propertyName, propValue);
		}
		return props;
	}

	private Object getPropertyValue(String propertyName) throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		Field declaredField = beanObject.getClass().getDeclaredField(propertyName);
		declaredField.setAccessible(true);
		Object value = declaredField.get(beanObject);

		declaredField.setAccessible(false);
		return value;
	}

}
