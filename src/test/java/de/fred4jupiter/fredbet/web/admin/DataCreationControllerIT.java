package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class DataCreationControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private DataPopulator dataPopulator;

    @Test
    public void showDataCreationPageWithTranslatedLabels() {
        assertThat(mvc.get().uri("/administration"))
            .hasStatusOk()
            .hasViewName("admin/data_creation")
            .bodyText().contains(
                "Test Data Creation",
                "football-data.org",
                "Number of Groups",
                "create demo bets for all users",
                "create demo results for all users",
                "Test Demo Users",
                "Number of test users",
                "Danger Zone",
                "Delete all users",
                "Delete all bets and matches",
                "Delete all bets, matches and teams",
                "For existing matches and users",
                "Import JSON test data",
                "Sample files:",
                "These actions cannot be undone.",
                "type=\"number\" min=\"1\" max=\"100\"")
            // thymeleaf renders a missing message key as ??key_locale??
            .doesNotContain("??");
    }

    @Test
    public void tooManyTestUsersShowsErrorAtField() {
        assertThat(mvc.post().uri("/administration/save").with(csrf()).param("numberOfTestUsers", "500"))
            .hasStatusOk()
            .hasViewName("admin/data_creation")
            .bodyText().contains("is-invalid", "invalid-feedback");

        verify(dataPopulator, never()).executeAsync(any());
    }

    @Test
    public void createDemoBetsShowsTranslatedMessage() {
        MvcTestResult result = mvc.get().uri("/administration/createDemoBets").exchange();

        assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/administration");
        WebMessageUtil.WebMessage message = (WebMessageUtil.WebMessage) result.getMvcResult().getFlashMap().get("globalMessage");
        assertThat(message.getText()).isEqualTo("Test bets created successfully!");
        verify(dataPopulator).executeAsync(any());
    }
}
