package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class LastLoginsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    public void showLastLogins() throws Exception {
        mockMvc.perform(get("/administration/lastlogins"))
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(LastLoginsController.class))
            .andExpect(content().string(allOf(
                containsString("Last logins of users ("),
                containsString("list-group"))));
    }

    @Test
    @WithMockUser(authorities = FredBetPermission.PERM_ADMINISTRATION)
    public void deniedWithoutPermission() throws Exception {
        // the administration permission alone is not enough, the page needs PERM_SHOW_LAST_LOGINS
        mockMvc.perform(get("/administration/lastlogins"))
            .andExpect(status().isForbidden());
    }
}
