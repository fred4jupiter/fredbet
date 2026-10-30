package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class SystemInfoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void showSystemInfoGroupedBySection() throws Exception {
        mockMvc.perform(get("/systeminfo").session(LoggedInSession.of(userDetailsService, "admin")))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("fa-cogs"),
                containsString("<span>Runtime</span>"),
                containsString("<dt class=\"col-sm-4 text-body-secondary fw-normal\">Host Name</dt>"),
                containsString("font-monospace"))));
    }
}
