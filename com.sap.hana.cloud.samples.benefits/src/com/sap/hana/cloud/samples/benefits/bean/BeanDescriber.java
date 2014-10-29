package com.sap.hana.cloud.samples.benefits.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class BeanDescriber {

	private Object beanObject;

	public BeanDescriber(Object beanObject) {
		this.beanObject = beanObject;
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

}
