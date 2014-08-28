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
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmExtension;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmSchemaView;

import com.sap.hana.cloud.samples.benefits.bean.BeanDescriber;
import com.sap.hana.cloud.samples.benefits.odata.AdministrationService;
import com.sap.hana.cloud.samples.benefits.odata.BenefitAmountService;
import com.sap.hana.cloud.samples.benefits.odata.CampaignService;
import com.sap.hana.cloud.samples.benefits.odata.OrderService;
import com.sap.hana.cloud.samples.benefits.odata.UserService;
import com.sap.hana.cloud.samples.benefits.odata.beans.BenefitsAmount;
import com.sap.hana.cloud.samples.benefits.odata.beans.StartCampaignDetails;
import com.sap.hana.cloud.samples.benefits.odata.beans.UIConfig;
import com.sap.hana.cloud.samples.benefits.odata.beans.UserInfo;

public class ProcessingExtension implements JPAEdmExtension {

	@SuppressWarnings("rawtypes")
	private static final Class[] SERVICES = { UserService.class, CampaignService.class, AdministrationService.class, OrderService.class,
			BenefitAmountService.class };

	@SuppressWarnings("rawtypes")
	private static final Map<Class, String> ODATA_TYPES = new HashMap<>();

	static {
		ODATA_TYPES.put(StartCampaignDetails.class, "StartCampaignDetails"); //$NON-NLS-1$
		ODATA_TYPES.put(UIConfig.class, "UIConfig"); //$NON-NLS-1$
		ODATA_TYPES.put(UserInfo.class, FunctionImportEntitySets.USER_INFO);
		ODATA_TYPES.put(BenefitsAmount.class, FunctionImportEntitySets.BENEFITS_AMOUNT);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void extendWithOperation(final JPAEdmSchemaView arg0) {
		for (Class service : SERVICES) {
			arg0.registerOperations(service, null);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void extendJPAEdmSchema(final JPAEdmSchemaView arg0) {
		Schema edmSchema = arg0.getEdmSchema();

		List<ComplexType> types = new ArrayList<>();
		for (Entry<Class, String> odataType : ODATA_TYPES.entrySet()) {
			try {
				types.add(describeType(odataType.getKey(), odataType.getValue()));
			} catch (SecurityException | IllegalArgumentException | InstantiationException | IllegalAccessException | NoSuchFieldException
					| IntrospectionException ex) {
				throw new IllegalStateException("Invalid OData configuration!", ex); //$NON-NLS-1$
			}
		}
		edmSchema.setComplexTypes(types);
	}

	private ComplexType describeType(Class odataType, String name) throws SecurityException, IllegalArgumentException, InstantiationException,
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

	private List<Field> getTypeFields(Class odataType) throws InstantiationException, IllegalAccessException, IntrospectionException,
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

}
