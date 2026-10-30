package de.fred4jupiter.fredbet.web.user.setting;

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
public class UserSettingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void showSettingsWithReadableThemeNames() throws Exception {
        mockMvc.perform(get("/setting").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("form-switch"),
                containsString("value=\"CYBORG\">Cyborg</option>"),
                containsString("value=\"LIGHT\">Light</option>"),
                containsString("reset to defaults"))));
    }

    @Test
    public void resetToDefaultsShowsDefaultTheme() throws Exception {
        mockMvc.perform(get("/setting/default").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("value=\"DEFAULT\" selected=\"selected\">Default</option>")));
    }

    private MockHttpSession englishSession() {
        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        return session;
    }
}
