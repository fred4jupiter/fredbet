package de.fred4jupiter.fredbet.web.integration;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.integration.Competition;
import de.fred4jupiter.fredbet.integration.FootballDataRuntimeSettings;
import de.fred4jupiter.fredbet.integration.FootballDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class FootballDataControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private FootballDataService footballDataService;

    private MockHttpSession session;

    @BeforeEach
    public void setup() {
        session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
    }

    @Test
    public void tournamentAndSyncLockedWhenNotActivated() throws Exception {
        when(footballDataService.loadSettings()).thenReturn(settings(false, null, null));

        mockMvc.perform(get("/footballdata").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("Activate the integration and save an API token first."),
                containsString("Select and save a tournament first."))));
    }

    @Test
    public void syncLockedWhenNoTournamentSelected() throws Exception {
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", null));

        mockMvc.perform(get("/footballdata").session(session))
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

        mockMvc.perform(get("/footballdata").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("FIFA World Cup - 2026"),
                not(containsString("Activate the integration and save an API token first.")),
                not(containsString("Select and save a tournament first.")))));
    }

    @Test
    public void syncRejectedWhenNoTournamentSelected() throws Exception {
        when(footballDataService.loadSettings()).thenReturn(settings(true, "token", null));

        mockMvc.perform(post("/footballdata/sync").session(session).param("_csrf", csrfToken()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/footballdata"))
            .andExpect(flash().attributeCount(1));
    }

    /**
     * Reads the CSRF token from the rendered page, as a browser would submit it.
     */
    private String csrfToken() throws Exception {
        String html = mockMvc.perform(get("/footballdata").session(session)).andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private FootballDataRuntimeSettings settings(boolean enabled, String apiToken, Competition competition) {
        FootballDataRuntimeSettings settings = new FootballDataRuntimeSettings();
        settings.setEnabled(enabled);
        settings.setApiToken(apiToken);
        settings.setCompetition(competition);
        return settings;
    }
}
