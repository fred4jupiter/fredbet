package de.fred4jupiter.fredbet.service.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;

@Service
public class ExcelImportService {

	public List<Match> importFromExcel(File file) {
		final List<Match> matches = new ArrayList<>();

		try {
			InputStream inp = new FileInputStream(file);
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) {
					continue;
				}
				Match match = convertRowToMatch(row);
				matches.add(match);
			}
		} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
			throw new ExcelReadingException(e.getMessage(), e);
		}

		return matches;
	}

	private Match convertRowToMatch(Row row) {
		String country1 = row.getCell(0).getStringCellValue();
		String country2 = row.getCell(1).getStringCellValue();
		String group = row.getCell(2).getStringCellValue();
		Date kickOffDate = HSSFDateUtil.getJavaDate(row.getCell(3).getNumericCellValue());
		String stadium = row.getCell(4).getStringCellValue();
		Match match = new Match();
		match.setCountryOne(Country.fromName(country1));
		match.setCountryTwo(Country.fromName(country2));
		match.setGroup(Group.valueOf(group));
		match.setKickOffDate(kickOffDate);
		match.setStadium(stadium);
		return match;
	}

}
