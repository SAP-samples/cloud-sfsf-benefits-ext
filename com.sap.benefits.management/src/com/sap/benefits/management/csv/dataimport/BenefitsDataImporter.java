package com.sap.benefits.management.csv.dataimport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import com.googlecode.jcsv.CSVStrategy;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.sap.benefits.management.persistence.BenefitDAO;
import com.sap.benefits.management.persistence.model.Benefit;

public class BenefitsDataImporter {

	public void importData(String filepath) throws IOException{
		final InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(filepath);
		final Reader csvFile = new InputStreamReader(resourceAsStream);

		CSVStrategy strategy = new CSVStrategy(',', '"', '#', true, true);
		final CSVReader<Benefit> personReader = new CSVReaderBuilder<Benefit>(csvFile).entryParser(new BenefitParser()).strategy(strategy).build();
		final List<Benefit> benefits = personReader.readAll();
		
		persistBenefits(benefits);
	}

	private void persistBenefits(List<Benefit> benefits) {
		final BenefitDAO dao = new BenefitDAO();
		dao.deleteAll();
		
		for (Benefit benefit : benefits) {
			dao.save(benefit);
		}
	}

}
