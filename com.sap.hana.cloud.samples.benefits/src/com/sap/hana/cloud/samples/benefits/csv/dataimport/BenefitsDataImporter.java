package com.sap.hana.cloud.samples.benefits.csv.dataimport;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.sap.hana.cloud.samples.benefits.persistence.BenefitDAO;
import com.sap.hana.cloud.samples.benefits.persistence.model.Benefit;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;

public class BenefitsDataImporter {

	public void importDataFromCSV(String filepath) throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		if (classLoader == null) {
			throw new IllegalStateException("Cannot import data - null classloader");
		}
		Reader fileReader = new InputStreamReader(classLoader.getResourceAsStream(filepath), Charset.forName("UTF-8"));

		try (CSVReader csvFile = new CSVReader(fileReader)) {
			final List<Benefit> benefits = readBenefits(csvFile);
			persistBenefits(benefits);
		}

	}

	private List<Benefit> readBenefits(CSVReader reader) throws NumberFormatException, IOException {
		final List<Benefit> benefits = new ArrayList<Benefit>();
		String[] nextLine;
		reader.readNext();

		while ((nextLine = reader.readNext()) != null) {

			if (nextLine.length != 6) {
				throw new IllegalArgumentException("data is not a valid benefit record");
			}

			final Benefit benefit = new Benefit();
			benefit.setName(nextLine[0]);
			benefit.setDescription(nextLine[1]);
			benefit.setLink(nextLine[2]);

			final BenefitType type = new BenefitType();
			type.setName(nextLine[3]);
			type.setValue(Long.parseLong(nextLine[4]));
			type.setActive(Boolean.parseBoolean(nextLine[5]));

			benefit.addType(type);
			benefits.add(benefit);
		}

		return benefits;
	}

	private void persistBenefits(List<Benefit> benefits) {
		final BenefitDAO dao = new BenefitDAO();
		dao.deleteAll();

		for (Benefit benefit : benefits) {
			dao.save(benefit);
		}
	}
}
