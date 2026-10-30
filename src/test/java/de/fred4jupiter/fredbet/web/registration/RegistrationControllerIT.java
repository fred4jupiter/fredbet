package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.registration.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

        mockMvc.perform(get("/registration").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("registration-card"),
                containsString("autocomplete=\"new-password\""),
                containsString("Back to login"))));
    }

    @Test
    public void registrationWithMismatchingPasswordsShowsErrorAtField() throws Exception {
        when(registrationService.isSelfRegistrationEnabled()).thenReturn(true);
        MockHttpSession session = englishSession();

        mockMvc.perform(post("/registration").session(session).param("_csrf", csrfToken(session))
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
        MockHttpSession session = englishSession();

        mockMvc.perform(post("/registration").session(session).param("_csrf", csrfToken(session))
                .param("token", "abc").param("username", "anna").param("password", "secret").param("passwordRepeat", "secret"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(registrationService).registerNewUser("anna", "secret", false);
    }

    /**
     * Reads the CSRF token from the rendered page, as a browser would submit it.
     */
    private String csrfToken(MockHttpSession session) throws Exception {
        String html = mockMvc.perform(get("/registration").session(session)).andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private MockHttpSession englishSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        return session;
    }
}
