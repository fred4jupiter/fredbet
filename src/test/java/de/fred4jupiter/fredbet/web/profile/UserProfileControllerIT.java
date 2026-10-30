package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class UserProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showChangePasswordForm() throws Exception {
        mockMvc.perform(get("/profile/changePassword"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("<h1>Change password</h1>"),
                containsString("autocomplete=\"current-password\""),
                containsString("Repeat new password"))));
    }

    @Test
    public void changePasswordWithMismatchingPasswordsShowsErrorAtField() throws Exception {
        mockMvc.perform(post("/profile/changePassword").with(csrf())
                .param("oldPassword", "whatever").param("newPassword", "secret1").param("newPasswordRepeat", "secret2"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("is-invalid"),
                containsString("invalid-feedback"))));
    }

    @Test
    public void showChangeUsernameForm() throws Exception {
        mockMvc.perform(get("/profile/changeUsername"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("<h1>Change username</h1>"),
                containsString("maxlength=\"12\""))));
    }

    @Test
    public void changeUsernameWithTooShortNameShowsErrorAtField() throws Exception {
        mockMvc.perform(post("/profile/changeUsername").with(csrf())
                .param("newUsername", "x"))
            .andExpect(status().isOk())
            .andExpect(content().string(allOf(
                containsString("is-invalid"),
                containsString("invalid-feedback"))));
    }

}
