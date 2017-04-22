package de.fred4jupiter.fredbet.service.excel;

import java.io.ByteArrayInputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.MatchRepository;

@Service
public class ExcelImportService {

	@Autowired
	private MatchRepository matchRepository;

	public List<Match> importFromExcel(File file) {
		try (InputStream inp = new FileInputStream(file)) {
			return importFromInputStream(inp);
		} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
			throw new ExcelReadingException(e.getMessage(), e);
		}
	}

	public List<Match> importFromExcel(byte[] bytes) {
		try (InputStream inp = new ByteArrayInputStream(bytes)) {
			return importFromInputStream(inp);
		} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
			throw new ExcelReadingException(e.getMessage(), e);
		}
	}

	@Transactional
	public void importMatchesFromExcelAndSave(byte[] bytes) {
		List<Match> matches = importFromExcel(bytes);
		matchRepository.save(matches);
	}

	private List<Match> importFromInputStream(InputStream inp) throws IOException, InvalidFormatException {
		final List<Match> matches = new ArrayList<>();

		try (Workbook wb = WorkbookFactory.create(inp)) {
			Sheet sheet = wb.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) {
					continue;
				}
				Match match = convertRowToMatch(row);
				if (match != null) {
					matches.add(match);
				}
			}

			return matches;
		}
	}

	private Match convertRowToMatch(Row row) {
		if (row == null || row.getCell(0) == null) {
			return null;
		}

		String country1 = row.getCell(0).getStringCellValue();
		String country2 = row.getCell(1).getStringCellValue();
		String group = row.getCell(2).getStringCellValue();
		Date kickOffDate = HSSFDateUtil.getJavaDate(row.getCell(3).getNumericCellValue());
		String stadium = row.getCell(4).getStringCellValue();

		Match match = new Match();

		if (Country.fromName(country1) != null) {
			match.setCountryOne(Country.fromName(country1));
		} else {
			match.setTeamNameOne(country1);
		}

		if (Country.fromName(country2) != null) {
			match.setCountryTwo(Country.fromName(country2));
		} else {
			match.setTeamNameTwo(country2);
		}

		match.setGroup(Group.valueOf(group));
		match.setKickOffDate(kickOffDate);
		match.setStadium(stadium);
		return match;
	}

}
