package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
public class LastLoginsControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    @WithUserDetails("admin")
    public void showLastLogins() {
        MvcTestResult result = mvc.get().uri("/administration/lastlogins").exchange();

        assertThat(result).hasStatusOk().handler().hasType(LastLoginsController.class);
        assertThat(result).bodyText().contains(
            "Last logins of users (",
            "list-group");
    }

    @Test
    @WithMockUser(authorities = FredBetPermission.PERM_ADMINISTRATION)
    public void deniedWithoutPermission() {
        // the administration permission alone is not enough, the page needs PERM_SHOW_LAST_LOGINS
        assertThat(mvc.get().uri("/administration/lastlogins"))
            .hasStatus(HttpStatus.FORBIDDEN);
    }
}
