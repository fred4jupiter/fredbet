package de.fred4jupiter.fredbet.web.user;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class UserListControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showUsersWithFilterAndEditLinks() {
        assertThat(mvc.get().uri("/user"))
            .hasStatusOk()
            .bodyText().contains(
                "Users (",
                "placeholder=\"Search user\"",
                "data-username=\"admin\"",
                "stretched-link",
                "Create user");
    }
}
