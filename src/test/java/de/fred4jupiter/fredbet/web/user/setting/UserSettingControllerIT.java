package de.fred4jupiter.fredbet.web.user.setting;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class UserSettingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showSettingsWithReadableThemeNames() throws Exception {
        mockMvc.perform(get("/setting"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("form-switch"),
                containsString("value=\"CYBORG\">Cyborg</option>"),
                containsString("value=\"LIGHT\">Light</option>"),
                containsString("reset to defaults"))));
    }

    @Test
    public void resetToDefaultsShowsDefaultTheme() throws Exception {
        mockMvc.perform(get("/setting/default"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("value=\"DEFAULT\" selected=\"selected\">Default</option>")));
    }

}
