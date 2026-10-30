package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class UserProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void showChangePasswordForm() throws Exception {
        mockMvc.perform(get("/profile/changePassword").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("<h1>Change password</h1>"),
                containsString("autocomplete=\"current-password\""),
                containsString("Repeat new password"))));
    }

    @Test
    public void changePasswordWithMismatchingPasswordsShowsErrorAtField() throws Exception {
        MockHttpSession session = englishSession();
        mockMvc.perform(post("/profile/changePassword").session(session).param("_csrf", csrfToken(session, "/profile/changePassword"))
                .param("oldPassword", "whatever").param("newPassword", "secret1").param("newPasswordRepeat", "secret2"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("is-invalid"),
                containsString("invalid-feedback"))));
    }

    @Test
    public void showChangeUsernameForm() throws Exception {
        mockMvc.perform(get("/profile/changeUsername").session(englishSession()))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("<h1>Change username</h1>"),
                containsString("maxlength=\"12\""))));
    }

    @Test
    public void changeUsernameWithTooShortNameShowsErrorAtField() throws Exception {
        MockHttpSession session = englishSession();
        mockMvc.perform(post("/profile/changeUsername").session(session).param("_csrf", csrfToken(session, "/profile/changeUsername"))
                .param("newUsername", "x"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("is-invalid"),
                containsString("invalid-feedback"))));
    }

    /**
     * Reads the CSRF token from the rendered page, as a browser would submit it.
     */
    private String csrfToken(MockHttpSession session, String page) throws Exception {
        String html = mockMvc.perform(get(page).session(session)).andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private MockHttpSession englishSession() {
        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        return session;
    }
}
