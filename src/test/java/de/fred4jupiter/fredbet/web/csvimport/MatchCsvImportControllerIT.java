package de.fred4jupiter.fredbet.web.csvimport;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class MatchCsvImportControllerIT {

    private static final String CSV = """
        Home,Away,Date,Venue
        Germany,France,11.06.2026 21:00,Allianz Arena
        """;

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void mappingStepLockedWithoutUpload() {
        assertThat(mvc.get().uri("/csvimport"))
            .hasStatusOk()
            .bodyText().contains("Upload a CSV file first.");
    }

    @Test
    public void uploadShowsMappingAndImportRedirectsToMatches() {
        MockHttpSession session = new MockHttpSession();

        MvcTestResult upload = mvc.post().uri("/csvimport/upload").multipart()
            .file(new MockMultipartFile("csvFile", "matches.csv", "text/csv", CSV.getBytes(StandardCharsets.UTF_8)))
            .session(session).with(csrf()).exchange();
        assertThat(upload).hasStatus3xxRedirection().hasRedirectedUrl("/csvimport");

        assertThat(mvc.get().uri("/csvimport").session(session))
            .hasStatusOk()
            .bodyText().contains("matches.csv", "Allianz Arena", "Import matches")
            .doesNotContain("Upload a CSV file first.");

        MvcTestResult importResult = mvc.post().uri("/csvimport/import").session(session).with(csrf())
            .param("columnMappings[0]", "TEAM_ONE")
            .param("columnMappings[1]", "TEAM_TWO")
            .param("columnMappings[2]", "KICK_OFF_DATE")
            .param("columnMappings[3]", "VENUE")
            .exchange();
        assertThat(importResult).hasStatus3xxRedirection().hasRedirectedUrl("/matches");
    }

    @Test
    public void importErrorsAreShown() {
        MockHttpSession session = new MockHttpSession();
        mvc.post().uri("/csvimport/upload").multipart()
            .file(new MockMultipartFile("csvFile", "matches.csv", "text/csv", CSV.getBytes(StandardCharsets.UTF_8)))
            .session(session).with(csrf()).exchange();

        assertThat(mvc.post().uri("/csvimport/import").session(session).with(csrf())
            .param("columnMappings[0]", "TEAM_ONE")
            .param("columnMappings[1]", "")
            .param("columnMappings[2]", "KICK_OFF_DATE")
            .param("columnMappings[3]", "")
            .exchange())
            .hasStatusOk()
            .bodyText().contains("Nothing has been imported", "The required field Team 2 is not mapped to a column.");
    }

    @Test
    public void uploadWithoutFileIsRejected() {
        MvcTestResult result = mvc.post().uri("/csvimport/upload").multipart().with(csrf()).exchange();

        assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/csvimport");
        assertThat(result).flash().hasSize(1);
    }

    @Test
    @WithMockUser
    public void notAllowedForNormalUsers() {
        assertThat(mvc.get().uri("/csvimport")).hasStatus(HttpStatus.FORBIDDEN);
    }
}
