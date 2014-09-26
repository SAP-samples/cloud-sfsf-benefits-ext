package com.sap.hana.cloud.samples.benefits.csv.dataimport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.sap.hana.cloud.samples.benefits.persistence.BenefitDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitInfo;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;

public class BenefitsDataImporter {

	public void importDataFromCSV(String filepath) throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		if (classLoader == null) {
			throw new IllegalStateException("Cannot import data - null classloader"); //$NON-NLS-1$
		}
		Reader fileReader = new InputStreamReader(classLoader.getResourceAsStream(filepath), Charset.forName("UTF-8")); //$NON-NLS-1$

		try (CSVReader csvFile = new CSVReader(fileReader)) {
			final List<BenefitInfo> benefitsInfo = readBenefitsInfo(csvFile);
			persistBenefitsInfo(benefitsInfo);
		}

	}

	private List<BenefitInfo> readBenefitsInfo(CSVReader reader) throws NumberFormatException, IOException {
		final List<BenefitInfo> benefitsInfo = new ArrayList<>();
		String[] nextLine;
		reader.readNext();

		while ((nextLine = reader.readNext()) != null) {

			if (nextLine.length != 6) {
				throw new IllegalArgumentException("data is not a valid benefit record"); //$NON-NLS-1$
			}

			final BenefitInfo benefitInfo = new BenefitInfo();
			benefitInfo.setName(nextLine[0]);
			benefitInfo.setDescription(nextLine[1]);
			benefitInfo.setLink(nextLine[2]);

			final BenefitType type = new BenefitType();
			type.setName(nextLine[3]);
			type.setValue(Long.parseLong(nextLine[4]));
			type.setActive(Boolean.parseBoolean(nextLine[5]));

			benefitInfo.addType(type);
			benefitsInfo.add(benefitInfo);
		}

		return benefitsInfo;
	}

	private void persistBenefitsInfo(List<BenefitInfo> benefits) {
		final BenefitDAO dao = new BenefitDAO();
		dao.deleteAll();

		for (BenefitInfo benefitInfo : benefits) {
			dao.save(benefitInfo);
		}
	}
}
