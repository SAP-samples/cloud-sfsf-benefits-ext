package com.sap.hana.cloud.samples.benefits.odata;

import static com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportParameters.USER_ID;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType.Type;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

import com.sap.hana.cloud.samples.benefits.connectivity.ECAPIConnector;
import com.sap.hana.cloud.samples.benefits.odata.beans.BenefitsAmount;
import com.sap.hana.cloud.samples.benefits.odata.cfg.FunctionImportNames;

public class BenefitAmountService extends ODataService {

	private ECAPIConnector odataConnector;

	public BenefitAmountService() {
		this.odataConnector = ECAPIConnector.getInstance();
	}

	@EdmFunctionImport(name = FunctionImportNames.BENEFIT_AMOUNT, returnType = @ReturnType(type = Type.COMPLEX))
	public BenefitsAmount obtainUserBenefitsAmount(@EdmFunctionImportParameter(name = USER_ID, type = EdmType.STRING) String userId) {
		return odataConnector.getUserBenefitsAmount(userId);
	}
}
