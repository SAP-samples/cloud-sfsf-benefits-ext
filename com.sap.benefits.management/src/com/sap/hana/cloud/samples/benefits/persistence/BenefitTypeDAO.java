package com.sap.hana.cloud.samples.benefits.persistence;

import com.sap.hana.cloud.samples.benefits.persistence.common.PersistenceManager;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;

public class BenefitTypeDAO extends BasicDAO<BenefitType>{

	public BenefitTypeDAO() {
		super(PersistenceManager.getInstance().getEntityManagerProvider());
	}

}
