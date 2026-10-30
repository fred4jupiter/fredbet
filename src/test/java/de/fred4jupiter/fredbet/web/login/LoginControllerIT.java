package de.fred4jupiter.fredbet.web.login;

import de.fred4jupiter.fredbet.admin.LoginLogoService;
import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginLogoService loginLogoService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void showLoginPageWithoutLogo() throws Exception {
        loginLogoService.deleteLoginLogo();

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("loginLogo"))
                .andExpect(content().string(allOf(
                        containsString("login-visual-default"),
                        containsString("autocomplete=\"current-password\""),
                        containsString("autocapitalize=\"none\""),
                        not(containsString("id=\"loginLogo\"")))));
    }

    @Test
    public void showLoginPageWithLogo() throws Exception {
        byte[] logo = "logo".getBytes(StandardCharsets.UTF_8);
        loginLogoService.saveImage(logo);

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginLogo", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logo)))
                .andExpect(content().string(allOf(
                        containsString("id=\"loginLogo\""),
                        containsString("login-visual-backdrop"),
                        not(containsString("login-visual-default")))));
    }

    @Test
    public void loginErrorWithAuthenticationExceptionAddsErrorMessage() throws Exception {
        MvcResult result = mockMvc.perform(get("/login/error")
                        .sessionAttr(WebAttributes.AUTHENTICATION_EXCEPTION, new BadCredentialsException("Bad credentials")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("globalMessage"))
                .andReturn();

        WebMessageUtil.WebMessage message = (WebMessageUtil.WebMessage) result.getFlashMap().get("globalMessage");
        assertThat(message.getCssClass()).isEqualTo("alert-danger");
        assertThat(message.getText()).isEqualTo("Bad credentials");
    }

    @Test
    public void loginErrorWithoutAuthenticationExceptionRedirectsWithoutMessage() throws Exception {
        mockMvc.perform(get("/login/error"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    public void logoutWithLoginLogoShowsLoginPage() throws Exception {
        loginLogoService.saveImage("logo".getBytes(StandardCharsets.UTF_8));

        MockHttpSession session = LoggedInSession.of(userDetailsService, "admin");
        String html = mockMvc.perform(get("/matches/upcoming").session(session)).andReturn().getResponse().getContentAsString();
        Matcher matcher = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"").matcher(html);
        assertThat(matcher.find()).isTrue();

        MvcResult logout = mockMvc.perform(post("/logout").session(session).param("_csrf", matcher.group(1)))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get(logout.getResponse().getRedirectedUrl()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"loginLogo\"")));
    }

    @Test
    public void errorPageIsReachableWithoutLogin() throws Exception {
        // otherwise every error of an anonymous request (e.g. on the login page) ends in an AccessDenied error
        int status = mockMvc.perform(get("/error")).andReturn().getResponse().getStatus();
        assertThat(status).isNotIn(302, 401, 403);
    }
}
