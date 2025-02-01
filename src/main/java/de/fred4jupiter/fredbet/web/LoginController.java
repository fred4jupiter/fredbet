package de.fred4jupiter.fredbet.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String loginPage() {
        return "login";
    }

    @GetMapping("/error")
    public String loginError(Model model, HttpServletRequest httpServletRequest) {
        Object authException = httpServletRequest.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (authException instanceof AuthenticationException authenticationException) {
            model.addAttribute("errorMsg", authenticationException.getLocalizedMessage());
        }

        model.addAttribute("loginError", true);
        return "login";
    }
}
