package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
public class ConnectionInfoControllerIT {

    private static final String NORMAL_USER = "connectionInfoUser";

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void createNormalUser() {
        userService.createUserIfNotExists(AppUserBuilder.create()
                .withUsernameAndPassword(NORMAL_USER, "secret")
                .withUserGroup(FredBetUserGroup.ROLE_USER)
                .build());
    }

    @Test
    @WithUserDetails(value = NORMAL_USER, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void normalUserCanViewConnectionInfo() {
        assertThat(mvc.get().uri("/info/connection")).hasStatusOk().hasViewName("info/connection")
                .bodyText()
                .contains("id=\"connectionInfo\"",
                        "data-context-root=\"/\"",
                        "id=\"connectionBaseUrl\"",
                        "/js/pages/connection_info-",
                        "href=\"/info/connection\"");
    }

    @Test
    public void anonymousUserIsRedirectedToLogin() {
        assertThat(mvc.get().uri("/info/connection")).hasStatus3xxRedirection()
                .redirectedUrl().endsWith("/login");
    }
}
