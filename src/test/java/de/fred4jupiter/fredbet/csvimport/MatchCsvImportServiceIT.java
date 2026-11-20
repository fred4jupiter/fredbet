package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static de.fred4jupiter.fredbet.csvimport.CsvImportField.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@TransactionalIntegrationTest
public class MatchCsvImportServiceIT {

    private static final String CSV = """
        Home Team;Away Team;Gruppe;Date;Stadium;Notes
        Deutschland;France;Group A;11.06.2026 21:00;Allianz Arena;opening
        CSV United FC;Spain;Achtelfinale;2026-06-12T18:00;;
        """;

    @Autowired
    private MatchCsvImportService matchCsvImportService;

    @Autowired
    private MatchService matchService;

    @Test
    public void suggestMappingByHeaders() {
        CsvTable table = parse(CSV);

        assertThat(matchCsvImportService.suggestMapping(table.headers()))
            .containsExactly(TEAM_ONE, TEAM_TWO, GROUP, KICK_OFF_DATE, VENUE, null);
    }

    @Test
    public void importMatchesAndResolveTeamBundleEntries() {
        CsvTable table = parse(CSV);
        int matchCountBefore = matchService.findAll().size();

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, matchCsvImportService.suggestMapping(table.headers()), null);

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.importedMatches()).isEqualTo(2);
        assertThat(result.warnings()).isEmpty();
        assertThat(matchService.findAll()).hasSize(matchCountBefore + 2);

        Match opening = findImported(LocalDateTime.of(2026, 6, 11, 21, 0), Country.FRANCE);
        assertThat(opening.getTeamOne().getTeamBundleEntry()).isEqualTo(Country.GERMANY);
        assertThat(opening.getTeamTwo().getTeamBundleEntry()).isEqualTo(Country.FRANCE);
        assertThat(opening.getGroup()).isEqualTo(Group.GROUP_A);
        assertThat(opening.getVenue()).isEqualTo("Allianz Arena");

        Match second = findImported(LocalDateTime.of(2026, 6, 12, 18, 0), Country.SPAIN);
        assertThat(second.getTeamOne().getTeamBundleEntry()).isNull();
        assertThat(second.getTeamOne().getName()).isEqualTo("CSV United FC");
        assertThat(second.getTeamTwo().getTeamBundleEntry()).isEqualTo(Country.SPAIN);
        assertThat(second.getGroup()).isEqualTo(Group.ROUND_OF_SIXTEEN);
        assertThat(second.getVenue()).isNull();
    }

    @Test
    public void nothingIsImportedIfALineIsInvalid() {
        CsvTable table = parse("""
            Home,Away,Group,Date,Home Crest
            Germany,France,A,11.06.2026 21:00,
            ,Spain,Group Z,someday,ftp://example.org/crest.svg
            """);
        int matchCountBefore = matchService.findAll().size();

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, List.of(TEAM_ONE, TEAM_TWO, GROUP, KICK_OFF_DATE, TEAM_ONE_IMAGE), null);

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors())
            .extracting(CsvImportError::lineNumber, CsvImportError::msgKey, CsvImportError::field)
            .containsExactly(
                tuple(3, "csvimport.error.valueMissing", TEAM_ONE),
                tuple(3, "csvimport.error.invalidKickOffDate", KICK_OFF_DATE),
                tuple(3, "csvimport.error.unknownGroup", GROUP),
                tuple(3, "csvimport.error.invalidImageUrl", TEAM_ONE_IMAGE));
        assertThat(matchService.findAll()).hasSize(matchCountBefore);
    }

    @Test
    public void mappingIsValidated() {
        CsvTable table = parse(CSV);

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, Arrays.asList(TEAM_ONE, TEAM_ONE, null, null, null, null), "{invalid");

        assertThat(result.errors())
            .extracting(CsvImportError::msgKey, CsvImportError::field)
            .containsExactlyInAnyOrder(
                tuple("csvimport.error.fieldMappedTwice", TEAM_ONE),
                tuple("csvimport.error.requiredFieldNotMapped", TEAM_TWO),
                tuple("csvimport.error.requiredFieldNotMapped", KICK_OFF_DATE),
                tuple("csvimport.error.invalidDateFormat", KICK_OFF_DATE));
    }

    private Match findImported(LocalDateTime kickOffDate, Country teamTwo) {
        return matchService.findAll().stream()
            .filter(match -> kickOffDate.equals(match.getKickOffDate()) && teamTwo.equals(match.getTeamTwo().getTeamBundleEntry()))
            .findFirst().orElseThrow();
    }

    private CsvTable parse(String csv) {
        return matchCsvImportService.parseCsv(csv.getBytes(StandardCharsets.UTF_8));
    }
}
