package de.fred4jupiter.fredbet.excel;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class ExcelImportServiceIT {

    @Autowired
    private ExcelImportService excelImportService;

    @Test
    public void readExcelFileWithMatchesToImport() {
        List<Match> matches = excelImportService.importFromExcel(new File("src/test/resources/excelimport/MatchesImportTemplate.xlsx"));
        assertNotNull(matches);
        assertFalse(matches.isEmpty());
        assertEquals(3, matches.size());

        Match match1 = matches.get(0);
        assertEquals(Country.GERMANY, match1.getTeamOne().getCountry());
        assertEquals(Country.BELGIUM, match1.getTeamTwo().getCountry());
        assertEquals(Group.GROUP_B, match1.getGroup());
        assertEquals(LocalDateTime.of(2017, 6, 1, 18, 0), match1.getKickOffDate());
        assertEquals("Dortmund", match1.getStadium());

        Match match2 = matches.get(1);
        assertEquals(Country.ANDORRA, match2.getTeamOne().getCountry());
        assertEquals(Country.ISRAEL, match2.getTeamTwo().getCountry());
        assertEquals(Group.GROUP_E, match2.getGroup());
        assertEquals(LocalDateTime.of(2018, 2, 5, 20, 0), match2.getKickOffDate());
        assertEquals("München", match2.getStadium());

        Match match3 = matches.get(2);
        assertEquals("Sieger Gruppe A", match3.getTeamOne().getName());
        assertEquals("Zweiter Gruppe B", match3.getTeamTwo().getName());
        assertEquals(Group.SEMI_FINAL, match3.getGroup());
        assertEquals(LocalDateTime.of(2017, 6, 28, 20, 0), match3.getKickOffDate());
        assertEquals("Kasan", match3.getStadium());
    }

}
