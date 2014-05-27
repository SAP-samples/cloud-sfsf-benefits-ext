package com.sap.hana.cloud.samples.benefits.odata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.ComplexType;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmExtension;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmSchemaView;

public class ProcessingExtension implements JPAEdmExtension {

	@Override
	public void extendJPAEdmSchema(final JPAEdmSchemaView arg0) {
		Schema edmSchema = arg0.getEdmSchema();
		List<ComplexType> types = new ArrayList<>();
		types.add(getUserInfoType());
		types.add(getStartCampaignType());
		types.add(getUIConfigType());
		edmSchema.setComplexTypes(types);
	}

	@Override
	public void extendWithOperation(final JPAEdmSchemaView arg0) {
		arg0.registerOperations(UserFunctionImport.class, null);
		arg0.registerOperations(CampaignFunctionImport.class, null);
		arg0.registerOperations(SystemFunctionImport.class, null);
		arg0.registerOperations(OrderFunctionImport.class, null);
	}

	private ComplexType getStartCampaignType() {
		List<Property> properties = new ArrayList<>();
		properties.add(createProperty("canBeStarted", EdmSimpleTypeKind.Boolean));
		properties.add(createProperty("campaignId", EdmSimpleTypeKind.Int64));
		properties.add(createProperty("startedCampaignName", EdmSimpleTypeKind.String));

		ComplexType startCampaignType = new ComplexType();
		startCampaignType.setName("StartCampaignDetails");
		startCampaignType.setProperties(properties);

		return startCampaignType;
	}

	private ComplexType getUserInfoType() {
		ComplexType userInfoType = new ComplexType();
		List<Property> properties = new ArrayList<>();
		List<String> propertyNames = Arrays.asList("firstName", "lastName", "title", "department", "division", "location", "businessPhone");
		for (String name : propertyNames) {
			properties.add(createProperty(name, EdmSimpleTypeKind.String));
		}

		userInfoType.setName("UserInfo");
		userInfoType.setProperties(properties);

		return userInfoType;

	}

	private ComplexType getUIConfigType() {
		ComplexType uiConfigType = new ComplexType();
		List<Property> properties = new ArrayList<>();
		List<String> propertyNames = Arrays.asList("showEmployeesTile", "showBenefitsTile", "showCampaignTile", "showOrderTile", "showInfoTile");

		for (String name : propertyNames) {
			properties.add(createProperty(name, EdmSimpleTypeKind.Boolean));
		}

		uiConfigType.setName("UIConfig");
		uiConfigType.setProperties(properties);

		return uiConfigType;

	}

	private SimpleProperty createProperty(String propertyName, EdmSimpleTypeKind propertyType) {
		SimpleProperty property = new SimpleProperty();
		property.setName(propertyName);
		property.setType(propertyType);
		return property;
	}

}
