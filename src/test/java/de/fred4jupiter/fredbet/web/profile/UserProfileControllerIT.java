package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class UserProfileControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    public void showChangePasswordForm() {
        assertThat(mvc.get().uri("/profile/changePassword"))
            .hasStatusOk()
            .bodyText().contains(
                "<h1>Change password</h1>",
                "autocomplete=\"current-password\"",
                "Repeat new password");
    }

    @Test
    public void changePasswordWithMismatchingPasswordsShowsErrorAtField() {
        assertThat(mvc.post().uri("/profile/changePassword").with(csrf())
                .param("oldPassword", "whatever").param("newPassword", "secret1").param("newPasswordRepeat", "secret2"))
            .hasStatusOk()
            .bodyText().contains("is-invalid", "invalid-feedback");
    }

    @Test
    public void showChangeUsernameForm() {
        assertThat(mvc.get().uri("/profile/changeUsername"))
            .hasStatusOk()
            .bodyText().contains(
                "<h1>Change username</h1>",
                "maxlength=\"12\"");
    }

    @Test
    public void changeUsernameWithTooShortNameShowsErrorAtField() {
        assertThat(mvc.post().uri("/profile/changeUsername").with(csrf())
                .param("newUsername", "x"))
            .hasStatusOk()
            .bodyText().contains("is-invalid", "invalid-feedback");
    }

}
