package de.fred4jupiter.fredbet.service.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;
import de.fred4jupiter.fredbet.domain.Match;

public class ExcelImportServiceIT extends AbstractIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelImportServiceIT.class);

	@Autowired
	private ExcelImportService excelImportService;

	@Test
	public void readExcelFileWithMatchesToImport() {
		List<Match> matches = excelImportService.importFromExcel(new File("src/test/resources/excelimport/MatchesImportTemplate.xlsx"));
		assertNotNull(matches);
		assertFalse(matches.isEmpty());
		assertEquals(1, matches.size());

		Match match = matches.get(0);
		LOG.debug("match: {}", match);
	}

}
