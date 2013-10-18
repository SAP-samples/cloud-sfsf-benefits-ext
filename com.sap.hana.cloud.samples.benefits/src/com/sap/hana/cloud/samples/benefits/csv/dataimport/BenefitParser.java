package com.sap.hana.cloud.samples.benefits.csv.dataimport;

import java.math.BigDecimal;

import com.googlecode.jcsv.reader.CSVEntryParser;
import com.sap.hana.cloud.samples.benefits.persistence.model.Benefit;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;

public class BenefitParser implements CSVEntryParser<Benefit> {

	@Override
	public Benefit parseEntry(String... data) {
		if (data.length != 6) {
			throw new IllegalArgumentException("data is not a valid benefit record");
		}

		final Benefit benefit = new Benefit();
		benefit.setName(data[0]);
		benefit.setDescription(data[1]);
		benefit.setLink(data[2]);

		final BenefitType type = new BenefitType();
		type.setName(data[3]);
		final double valueAsDouble = Double.parseDouble(data[4]);
		type.setValue(BigDecimal.valueOf(valueAsDouble));
		type.setActive(Boolean.parseBoolean(data[5]));

		benefit.addType(type);

		return benefit;
	}

}
