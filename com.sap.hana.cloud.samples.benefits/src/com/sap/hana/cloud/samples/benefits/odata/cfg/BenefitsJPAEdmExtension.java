package com.sap.hana.cloud.samples.benefits.odata.cfg;

import java.io.InputStream;

import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmExtension;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmSchemaView;

import com.sap.hana.cloud.samples.benefits.odata.AdministrationService;
import com.sap.hana.cloud.samples.benefits.odata.BenefitAmountService;
import com.sap.hana.cloud.samples.benefits.odata.CampaignService;
import com.sap.hana.cloud.samples.benefits.odata.OrderService;
import com.sap.hana.cloud.samples.benefits.odata.UserService;

public class BenefitsJPAEdmExtension implements JPAEdmExtension {

	private static final Class<?>[] SERVICES = { UserService.class, CampaignService.class, AdministrationService.class, OrderService.class,
			BenefitAmountService.class };

	@Override
	public void extendWithOperation(final JPAEdmSchemaView view) {
		for (Class<?> service : SERVICES) {
			view.registerOperations(service, null);
		}
	}

	@Override
	public void extendJPAEdmSchema(final JPAEdmSchemaView view) {
		Schema edmSchema = view.getEdmSchema();
		edmSchema.setComplexTypes(ComplexTypesDescriber.getInstance().getEdmComplexTypes());
	}

	@Override
	public InputStream getJPAEdmMappingModelStream() {
		return null;
	}

}
