package de.fred4jupiter.fredbet.web.user.setting;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class UserSettingControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showSettingsWithReadableThemeNames() {
        assertThat(mvc.get().uri("/setting"))
            .hasStatusOk()
            .bodyText().contains(
                "form-switch",
                "value=\"CYBORG\">Cyborg</option>",
                "value=\"LIGHT\">Light</option>",
                "reset to defaults");
    }

    @Test
    public void resetToDefaultsShowsDefaultTheme() {
        assertThat(mvc.get().uri("/setting/default"))
            .hasStatusOk()
            .bodyText().contains("value=\"DEFAULT\" selected=\"selected\">Default</option>");
    }

}
