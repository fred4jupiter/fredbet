package de.fred4jupiter.fredbet.service.excel;

import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.util.DateUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelImportService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private DatabasePopulator dataBasePopulator;

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
        dataBasePopulator.deleteAllBetsAndMatches();

        List<Match> matches = importFromExcel(bytes);
        matchRepository.saveAll(matches);
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
        Date kickOffDate = DateUtil.getJavaDate(row.getCell(3).getNumericCellValue());
        String stadium = row.getCell(4).getStringCellValue();

        Match match = new Match();

        if (Country.fromName(country1) != null) {
            match.getTeamOne().setCountry(Country.fromName(country1));
        } else {
            match.getTeamOne().setName(country1);
        }

        if (Country.fromName(country2) != null) {
            match.getTeamTwo().setCountry(Country.fromName(country2));
        } else {
            match.getTeamTwo().setName(country2);
        }

        match.setGroup(Group.valueOf(group));
        match.setKickOffDate(DateUtils.toLocalDateTime(kickOffDate));
        match.setStadium(stadium);
        return match;
    }

}
