package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class ExcelExportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void showAllExportsAsDownloadLinks() throws Exception {
        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);

        mockMvc.perform(get("/excelexport/show").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("href=\"/excelexport/allBets\""),
                containsString("href=\"/excelexport/extraBets\""),
                containsString("href=\"/excelexport/ranking\""),
                containsString("href=\"/excelexport/pointsCount\""),
                containsString("All extra bets"),
                containsString("Ranking"))));
    }
}
