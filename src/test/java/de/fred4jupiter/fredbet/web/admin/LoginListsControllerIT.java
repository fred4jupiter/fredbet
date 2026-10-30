package de.fred4jupiter.fredbet.web.admin;

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
public class LoginListsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showLastLogins() throws Exception {
        mockMvc.perform(get("/administration/lastlogins"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("Last logins of users ("),
                containsString("list-group"))));
    }

    @Test
    public void showActiveUsers() throws Exception {
        mockMvc.perform(get("/administration/active/users"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("Currently logged-in users ("),
                containsString("list-group"))));
    }

}
