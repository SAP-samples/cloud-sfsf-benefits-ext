package com.sap.hana.cloud.samples.benefits.odata.cfg;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

import com.sap.hana.cloud.samples.benefits.bean.BeanDescriber;
import com.sap.hana.cloud.samples.benefits.odata.beans.BenefitsAmount;
import com.sap.hana.cloud.samples.benefits.odata.beans.StartCampaignDetails;
import com.sap.hana.cloud.samples.benefits.odata.beans.UIConfig;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;

public final class ComplexTypesDescriber {

	private static ComplexTypesDescriber INTANCE;

	private static final Map<Class<?>, String> CLASS_TYPES = new HashMap<>();

	static {
		CLASS_TYPES.put(StartCampaignDetails.class, "StartCampaignDetails"); //$NON-NLS-1$
		CLASS_TYPES.put(UIConfig.class, "UIConfig"); //$NON-NLS-1$
		CLASS_TYPES.put(UserInfo.class, FunctionImportEntitySets.USER_INFO);
		CLASS_TYPES.put(BenefitsAmount.class, FunctionImportEntitySets.BENEFITS_AMOUNT);
	}

	private final List<ComplexType> edmComplexTypes;

	private ComplexTypesDescriber() {
		this.edmComplexTypes = describeTypes();
	}

	public static ComplexTypesDescriber getInstance() {
		if (INTANCE == null) {
			INTANCE = new ComplexTypesDescriber();
		}
		return INTANCE;
	}

	private List<ComplexType> describeTypes() {
		List<ComplexType> types = new ArrayList<>();
		for (Entry<Class<?>, String> odataType : CLASS_TYPES.entrySet()) {
			try {
				types.add(describeType(odataType.getKey(), odataType.getValue()));
			} catch (SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException | NoSuchFieldException
					| IntrospectionException ex) {
				throw new IllegalStateException("Invalid OData configuration!", ex); //$NON-NLS-1$
			}
		}
		return types;
	}

	private ComplexType describeType(Class<?> odataType, String name) throws SecurityException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, IntrospectionException, NoSuchFieldException {
		List<Field> fields = getTypeFields(odataType);

		List<Property> properties = new ArrayList<>();
		for (Field field : fields) {
			properties.add(createProperty(field));
		}

		ComplexType complexType = new ComplexType();
		complexType.setName(name);
		complexType.setProperties(properties);

		return complexType;
	}

	private List<Field> getTypeFields(Class<?> odataType) throws InstantiationException, IllegalAccessException, IntrospectionException,
			NoSuchFieldException {
		BeanDescriber beanDescriber = new BeanDescriber(odataType.newInstance());
		List<String> propertyNames = beanDescriber.getPropertyNames();

		List<Field> fields = new ArrayList<>();
		for (String propName : propertyNames) {
			fields.add(odataType.getDeclaredField(propName));
		}
		return fields;
	}

	@SuppressWarnings("nls")
	private Property createProperty(Field field) {
		switch (field.getType().getCanonicalName()) {
		case "java.lang.String":
			return createProperty(field.getName(), EdmSimpleTypeKind.String);
		case "java.lang.Short":
		case "short":
			return createProperty(field.getName(), EdmSimpleTypeKind.Int16);
		case "java.lang.Integer":
		case "int":
			return createProperty(field.getName(), EdmSimpleTypeKind.Int32);
		case "java.lang.Long":
		case "long":
			return createProperty(field.getName(), EdmSimpleTypeKind.Int64);
		case "java.lang.Boolean":
		case "boolean":
			return createProperty(field.getName(), EdmSimpleTypeKind.Boolean);
		case "java.lang.Double":
		case "double":
			return createProperty(field.getName(), EdmSimpleTypeKind.Double);
		default:
			String errMsg = String.format("Unsupported type [%s} for property [%s]!", field.getType().getCanonicalName(), field.getName());
			throw new IllegalArgumentException(errMsg);
		}
	}

	private SimpleProperty createProperty(String propertyName, EdmSimpleTypeKind propertyType) {
		SimpleProperty property = new SimpleProperty();
		property.setName(propertyName);
		property.setType(propertyType);
		return property;
	}

	public List<ComplexType> getEdmComplexTypes() {
		return edmComplexTypes;
	}
}
