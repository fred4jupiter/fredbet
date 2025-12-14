package de.fred4jupiter.fredbet.web.login;

import de.fred4jupiter.fredbet.admin.LoginLogoService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Optional;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final WebMessageUtil webMessageUtil;

    private final LoginLogoService loginLogoService;

    public LoginController(WebMessageUtil webMessageUtil, LoginLogoService loginLogoService) {
        this.webMessageUtil = webMessageUtil;
        this.loginLogoService = loginLogoService;
    }

    @GetMapping
    public String loginPage(Model model) {
        Optional<byte[]> bytes = loginLogoService.loadLoginLogo();
        bytes.ifPresent(value -> model.addAttribute("loginLogo", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(value)));
        return "login";
    }

    @GetMapping("/error")
    public String loginError(Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirect) {
        Object authException = httpServletRequest.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (authException instanceof AuthenticationException authenticationException) {
            webMessageUtil.addErrorMsg(redirect, "login.error", authenticationException.getLocalizedMessage());
        }

        model.addAttribute("loginError", true);
        return "redirect:/login";
    }
}
