package de.fred4jupiter.fredbet.web.login;

import de.fred4jupiter.fredbet.admin.LoginLogoService;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@MockMvcIntegrationTest
public class LoginControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private LoginLogoService loginLogoService;

    @Test
    public void showLoginPageWithoutLogo() {
        loginLogoService.deleteLoginLogo();

        MvcTestResult result = mvc.get().uri("/login").exchange();

        assertThat(result).hasStatusOk().hasViewName("login")
                .model().doesNotContainKey("loginLogo");
        assertThat(result).bodyText()
                .contains(
                        "login-visual-default",
                        "autocomplete=\"current-password\"",
                        "autocapitalize=\"none\"")
                .doesNotContain("id=\"loginLogo\"");
    }

    @Test
    public void showLoginPageWithLogo() {
        byte[] logo = "logo".getBytes(StandardCharsets.UTF_8);
        loginLogoService.saveImage(logo);

        MvcTestResult result = mvc.get().uri("/login").exchange();

        assertThat(result).hasStatusOk().hasViewName("login")
                .model().containsEntry("loginLogo", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logo));
        assertThat(result).bodyText()
                .contains("id=\"loginLogo\"", "login-visual-backdrop")
                .doesNotContain("login-visual-default");
    }

    @Test
    public void loginErrorWithAuthenticationExceptionAddsErrorMessage() {
        MvcTestResult result = mvc.get().uri("/login/error")
                .sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, new BadCredentialsException("Bad credentials"))
                .exchange();

        assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/login");
        assertThat(result).flash().containsKey("globalMessage");

        WebMessageUtil.WebMessage message = (WebMessageUtil.WebMessage) result.getMvcResult().getFlashMap().get("globalMessage");
        assertThat(message.getCssClass()).isEqualTo("alert-danger");
        assertThat(message.getText()).isEqualTo("Bad credentials");
    }

    @Test
    public void loginErrorWithoutAuthenticationExceptionRedirectsWithoutMessage() {
        MvcTestResult result = mvc.get().uri("/login/error").exchange();

        assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/login");
        assertThat(result).flash().isEmpty();
    }

    @Test
    @WithUserDetails("admin")
    public void logoutWithLoginLogoShowsLoginPage() {
        loginLogoService.saveImage("logo".getBytes(StandardCharsets.UTF_8));
        MvcTestResult logout = mvc.post().uri("/logout").with(csrf()).exchange();
        assertThat(logout).hasStatus3xxRedirection();

        assertThat(mvc.get().uri(logout.getResponse().getRedirectedUrl()).with(anonymous()))
                .hasStatusOk()
                .bodyText().contains("id=\"loginLogo\"");
    }

    @Test
    public void errorPageIsReachableWithoutLogin() {
        // otherwise every error of an anonymous request (e.g. on the login page) ends in an AccessDenied error
        int status = mvc.get().uri("/error").exchange().getResponse().getStatus();
        assertThat(status).isNotIn(302, 401, 403);
    }
}
