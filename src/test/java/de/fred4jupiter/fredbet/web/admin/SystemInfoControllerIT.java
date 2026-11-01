package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class SystemInfoControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showSystemInfoGroupedBySection() {
        assertThat(mvc.get().uri("/systeminfo"))
            .hasStatusOk()
            .bodyText().contains(
                "fa-cogs",
                "<span>Runtime</span>",
                "<dt class=\"col-sm-4 text-body-secondary fw-normal\">Host Name</dt>",
                "font-monospace");
    }
}
