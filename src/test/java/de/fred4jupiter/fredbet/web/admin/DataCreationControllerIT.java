package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class DataCreationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DataPopulator dataPopulator;

    @Test
    public void showDataCreationPageWithTranslatedLabels() throws Exception {
        mockMvc.perform(get("/administration"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/data_creation"))
            .andExpect(content().string(allOf(
                containsString("Test Data Creation"),
                containsString("football-data.org"),
                containsString("Number of Groups"),
                containsString("create demo bets for all users"),
                containsString("create demo results for all users"),
                containsString("Test Demo Users"),
                containsString("Number of test users"),
                containsString("Danger Zone"),
                containsString("Delete all users"),
                containsString("Delete all bets and matches"),
                containsString("Delete all bets, matches and teams"),
                containsString("For existing matches and users"),
                containsString("Import JSON test data"),
                containsString("Sample files:"),
                containsString("These actions cannot be undone."),
                containsString("type=\"number\" min=\"1\" max=\"100\""),
                // thymeleaf renders a missing message key as ??key_locale??
                not(containsString("??")))));
    }

    @Test
    public void tooManyTestUsersShowsErrorAtField() throws Exception {
        mockMvc.perform(post("/administration/save").with(csrf()).param("numberOfTestUsers", "500"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/data_creation"))
            .andExpect(content().string(allOf(
                containsString("is-invalid"),
                containsString("invalid-feedback"))));

        verify(dataPopulator, never()).executeAsync(any());
    }

    @Test
    public void createDemoBetsShowsTranslatedMessage() throws Exception {
        MvcResult result = mockMvc.perform(get("/administration/createDemoBets"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/administration"))
            .andReturn();

        WebMessageUtil.WebMessage message = (WebMessageUtil.WebMessage) result.getFlashMap().get("globalMessage");
        assertThat(message.getText()).isEqualTo("Test bets created successfully!");
        verify(dataPopulator).executeAsync(any());
    }
}
