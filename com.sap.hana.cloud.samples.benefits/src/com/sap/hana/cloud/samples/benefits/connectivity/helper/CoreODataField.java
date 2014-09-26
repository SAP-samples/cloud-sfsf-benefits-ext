package com.sap.hana.cloud.samples.benefits.connectivity.helper;

@SuppressWarnings("nls")
public enum CoreODataField {
	RESULT("d"), EMPLOYEE_INFO_NAV("empInfo"), JOB_INFO_NAV("jobInfoNav"), ARR_RESULTS("results"), POSITION("position"), START_DATE("startDate");

	private String fieldName;

	private CoreODataField(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}
