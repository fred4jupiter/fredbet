package de.fred4jupiter.fredbet.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final WebMessageUtil webMessageUtil;

    public LoginController(WebMessageUtil webMessageUtil) {
        this.webMessageUtil = webMessageUtil;
    }

    @GetMapping
    public String loginPage() {
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
