package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.registration.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
public class RegistrationControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    public void showRegistrationForm() {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);

        assertThat(mvc.get().uri("/registration"))
            .hasStatusOk()
            .bodyText().contains(
                "registration-card",
                "autocomplete=\"new-password\"",
                "Back to login");
    }

    @Test
    public void registrationWithMismatchingPasswordsShowsErrorAtField() {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);

        assertThat(mvc.post().uri("/registration").with(csrf())
                .param("token", "abc").param("username", "anna").param("password", "secret1").param("passwordRepeat", "secret2"))
            .hasStatusOk()
            .bodyText().contains("is-invalid", "invalid-feedback");
    }

    @Test
    public void successfulRegistrationRedirectsToLogin() {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);
        when(registrationService.isTokenValid("abc")).thenReturn(true);

        assertThat(mvc.post().uri("/registration").with(csrf())
                .param("token", "abc").param("username", "anna").param("password", "secret").param("passwordRepeat", "secret"))
            .hasStatus3xxRedirection()
            .hasRedirectedUrl("/login");

        verify(registrationService).registerNewUser("anna", "secret", false);
    }

}
