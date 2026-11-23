package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
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
    public void importCrestGivenAsSvgContent() {
        String svg = "<svg xmlns=\"\"http://www.w3.org/2000/svg\"\" viewBox=\"\"0 0 10 10\"\"><circle cx=\"\"5\"\" cy=\"\"5\"\" r=\"\"4\"\"/></svg>";
        CsvTable table = parse("""
            Home,Away,Date,Home SVG,Home Crest
            SVG Crest United,Away Crest City,2026-07-01T18:00,"%s",https://invalid.example.org/crest.svg
            """.formatted(svg));

        assertThat(matchCsvImportService.suggestMapping(table.headers()))
            .containsExactly(TEAM_ONE, TEAM_TWO, KICK_OFF_DATE, TEAM_ONE_SVG, TEAM_ONE_IMAGE);

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, matchCsvImportService.suggestMapping(table.headers()), null);

        assertThat(result.isSuccessful()).isTrue();
        // the SVG content takes precedence, so the URL is not downloaded
        assertThat(result.warnings()).isEmpty();
        Match match = matchService.findAll().stream()
            .filter(m -> "SVG Crest United".equals(m.getTeamOne().getName()))
            .findFirst().orElseThrow();
        assertThat(match.getTeamOne().getSvgContent()).isEqualTo(svg.replace("\"\"", "\""));
    }

    @Test
    public void invalidSvgContentIsRejected() {
        CsvTable table = parse("""
            Home,Away,Date,Away SVG
            Germany,France,2026-07-01T18:00,"<svg xmlns=""http://www.w3.org/2000/svg""><script>alert(1)</script></svg>"
            Germany,Spain,2026-07-02T18:00,no svg at all
            """);

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, matchCsvImportService.suggestMapping(table.headers()), null);

        assertThat(result.isSuccessful()).isFalse();
        assertThat(result.errors())
            .extracting(CsvImportError::lineNumber, CsvImportError::msgKey, CsvImportError::field)
            .containsExactly(
                tuple(2, "csvimport.error.invalidCrest", TEAM_TWO_SVG),
                tuple(3, "csvimport.error.invalidCrest", TEAM_TWO_SVG));
    }

    @Test
    public void importCrestGivenAsBase64EncodedRasterImage() {
        String pngDataUri = "data:image/png;base64," + Base64.getEncoder().encodeToString(createImage("png", BufferedImage.TYPE_INT_ARGB));
        String plainJpegBase64 = Base64.getEncoder().encodeToString(createImage("jpg", BufferedImage.TYPE_INT_RGB));
        // a data URI contains a comma, so it has to be quoted
        CsvTable table = parse("""
            Home,Away,Date,Home SVG,Away SVG
            PNG Crest United,JPEG Crest City,2026-07-03T18:00,"%s",%s
            """.formatted(pngDataUri, plainJpegBase64));

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, matchCsvImportService.suggestMapping(table.headers()), null);

        assertThat(result.isSuccessful()).isTrue();
        Match match = matchService.findAll().stream()
            .filter(m -> "PNG Crest United".equals(m.getTeamOne().getName()))
            .findFirst().orElseThrow();
        // raster images are wrapped into an SVG, the same way as for a crest uploaded on the team page
        assertThat(match.getTeamOne().getSvgContent()).startsWith("<svg").contains("href=\"data:image/png;base64,");
        assertThat(match.getTeamTwo().getSvgContent()).startsWith("<svg").contains("href=\"data:image/jpeg;base64,");
    }

    @Test
    public void suggestMappingPrefersBestMatchingHeader() {
        List<String> headers = List.of("match_id", "round", "group", "kickoff_datetime", "home_team", "away_team", "home_crest_url", "away_crest_url");

        assertThat(matchCsvImportService.suggestMapping(headers))
            .containsExactly(null, null, GROUP, KICK_OFF_DATE, TEAM_ONE, TEAM_TWO, TEAM_ONE_IMAGE, TEAM_TWO_IMAGE);
    }

    @Test
    public void urlInCrestColumnIsDownloaded() {
        CsvTable table = parse("""
            Home,Away,Date,Home SVG
            URL Crest United,Germany,2026-07-04T18:00,https://invalid.example.org/crest.png
            """);

        MatchCsvImportResult result = matchCsvImportService.importMatches(table, matchCsvImportService.suggestMapping(table.headers()), null);

        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.warnings())
            .extracting(CsvImportError::msgKey, CsvImportError::field, CsvImportError::value)
            .containsExactly(tuple("csvimport.warning.crestDownloadFailed", TEAM_ONE_IMAGE, "https://invalid.example.org/crest.png"));
    }

    @Test
    public void crestOfExistingTeamIsUpdated() {
        String firstSvg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 10 10\"><rect width=\"10\" height=\"10\"/></svg>";
        String secondSvg = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 10 10\"><circle cx=\"5\" cy=\"5\" r=\"4\"/></svg>";
        String csv = """
            Home,Away,Date,Home SVG,Home Crest
            Repeated Crest FC,Germany,2026-07-05T18:00,%s,%s
            """;

        matchCsvImportService.importMatches(parse(csv.formatted(quote(firstSvg), "")), List.of(TEAM_ONE, TEAM_TWO, KICK_OFF_DATE, TEAM_ONE_SVG, TEAM_ONE_IMAGE), null);
        MatchCsvImportResult result = matchCsvImportService.importMatches(parse(csv.formatted(quote(secondSvg), "")),
            List.of(TEAM_ONE, TEAM_TWO, KICK_OFF_DATE, TEAM_ONE_SVG, TEAM_ONE_IMAGE), null);
        // a failed download keeps the crest of the existing team
        MatchCsvImportResult failedDownload = matchCsvImportService.importMatches(parse(csv.formatted("", "https://invalid.example.org/crest.svg")),
            List.of(TEAM_ONE, TEAM_TWO, KICK_OFF_DATE, TEAM_ONE_SVG, TEAM_ONE_IMAGE), null);

        assertThat(result.createdTeams()).isZero();
        assertThat(failedDownload.warnings()).hasSize(1);
        assertThat(matchService.findAll())
            .filteredOn(match -> "Repeated Crest FC".equals(match.getTeamOne().getName()))
            .hasSize(3)
            .allSatisfy(match -> assertThat(match.getTeamOne().getSvgContent()).isEqualTo(secondSvg));
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

    private static byte[] createImage(String formatName, int imageType) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(new BufferedImage(10, 10, imageType), formatName, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String quote(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private CsvTable parse(String csv) {
        return matchCsvImportService.parseCsv(csv.getBytes(StandardCharsets.UTF_8));
    }
}
