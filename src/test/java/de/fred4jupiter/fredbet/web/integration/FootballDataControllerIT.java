package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.integration.Competition;
import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;
import de.fred4jupiter.fredbet.integration.FootballDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class FootballDataControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FootballDataService footballDataService;

    @Test
    public void tournamentAndSyncLockedWhenNotActivated() throws Exception {
        when(footballDataService.loadSettings()).thenReturn(settings(false, null, null));

        mockMvc.perform(get("/footballdata"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("Activate the integration and save an API token first."),
                containsString("Select and save a tournament first."))));
    }

    @Test
    public void syncLockedWhenNoTournamentSelected() throws Exception {
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", null));

        mockMvc.perform(get("/footballdata"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                not(containsString("Activate the integration and save an API token first.")),
                containsString("Fetch the competitions first"),
                containsString("Select and save a tournament first."))));
    }

    @Test
    public void allStepsUnlockedWhenTournamentSelected() throws Exception {
        Competition competition = new Competition(2000, "FIFA World Cup", "WC", 2026, "CUP");
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", competition));

        mockMvc.perform(get("/footballdata"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("FIFA World Cup - 2026"),
                not(containsString("Activate the integration and save an API token first.")),
                not(containsString("Select and save a tournament first.")))));
    }

    @Test
    public void syncRejectedWhenNoTournamentSelected() throws Exception {
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", null));

        mockMvc.perform(post("/footballdata/sync").with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/footballdata"))
            .andExpect(flash().attributeCount(1));
    }

    private FootballDataRuntimeSettings settings(boolean enabled, String apiToken, Competition competition) {
        FootballDataRuntimeSettings settings = new FootballDataRuntimeSettings();
        settings.setEnabled(enabled);
        settings.setApiToken(apiToken);
        settings.setCompetition(competition);
        return settings;
    }
}
