package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.integration.Competition;
import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;
import de.fred4jupiter.fredbet.integration.FootballDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class FootballDataControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private FootballDataService footballDataService;

    @Test
    public void tournamentAndSyncLockedWhenNotActivated() {
        when(footballDataService.loadSettings()).thenReturn(settings(false, null, null));

        assertThat(mvc.get().uri("/footballdata"))
            .hasStatusOk()
            .bodyText().contains(
                "Activate the integration and save an API token first.",
                "Select and save a tournament first.");
    }

    @Test
    public void syncLockedWhenNoTournamentSelected() {
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", null));

        assertThat(mvc.get().uri("/footballdata"))
            .hasStatusOk()
            .bodyText()
            .contains(
                "Fetch the competitions first",
                "Select and save a tournament first.")
            .doesNotContain("Activate the integration and save an API token first.");
    }

    @Test
    public void allStepsUnlockedWhenTournamentSelected() {
        Competition competition = new Competition(2000, "FIFA World Cup", "WC", 2026, "CUP");
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", competition));

        assertThat(mvc.get().uri("/footballdata"))
            .hasStatusOk()
            .bodyText()
            .contains("FIFA World Cup - 2026")
            .doesNotContain(
                "Activate the integration and save an API token first.",
                "Select and save a tournament first.");
    }

    @Test
    public void syncRejectedWhenNoTournamentSelected() {
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", null));

        MvcTestResult result = mvc.post().uri("/footballdata/sync").with(csrf()).exchange();

        assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/footballdata");
        assertThat(result).flash().hasSize(1);
    }

    private FootballDataRuntimeSettings settings(boolean enabled, String apiToken, Competition competition) {
        FootballDataRuntimeSettings settings = new FootballDataRuntimeSettings();
        settings.setEnabled(enabled);
        settings.setApiToken(apiToken);
        settings.setCompetition(competition);
        return settings;
    }
}
