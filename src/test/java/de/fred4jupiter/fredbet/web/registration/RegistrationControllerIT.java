package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.registration.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class RegistrationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    public void showRegistrationForm() throws Exception {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);

        mockMvc.perform(get("/registration"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("registration-card"),
                containsString("autocomplete=\"new-password\""),
                containsString("Back to login"))));
    }

    @Test
    public void registrationWithMismatchingPasswordsShowsErrorAtField() throws Exception {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);

        mockMvc.perform(post("/registration").with(csrf())
                .param("token", "abc").param("username", "anna").param("password", "secret1").param("passwordRepeat", "secret2"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("is-invalid"),
                containsString("invalid-feedback"))));
    }

    @Test
    public void successfulRegistrationRedirectsToLogin() throws Exception {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);
        when(registrationService.isTokenValid("abc")).thenReturn(true);

        mockMvc.perform(post("/registration").with(csrf())
                .param("token", "abc").param("username", "anna").param("password", "secret").param("passwordRepeat", "secret"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(registrationService).registerNewUser("anna", "secret", false);
    }

}
