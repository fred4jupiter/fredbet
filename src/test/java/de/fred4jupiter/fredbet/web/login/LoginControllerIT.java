package de.fred4jupiter.fredbet.web.login;

import de.fred4jupiter.fredbet.admin.LoginLogoService;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class LoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginLogoService loginLogoService;

    @Test
    public void showLoginPageWithoutLogo() throws Exception {
        loginLogoService.deleteLoginLogo();

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("loginLogo"));
    }

    @Test
    public void showLoginPageWithLogo() throws Exception {
        byte[] logo = "logo".getBytes(StandardCharsets.UTF_8);
        loginLogoService.saveImage(logo);

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginLogo", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(logo)));
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
}
