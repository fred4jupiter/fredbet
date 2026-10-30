package de.fred4jupiter.fredbet.web.user;

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
public class UserListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showUsersWithFilterAndEditLinks() throws Exception {

        mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("Users ("),
                containsString("placeholder=\"Search user\""),
                containsString("data-username=\"admin\""),
                containsString("stretched-link"),
                containsString("Create user"))));
    }
}
