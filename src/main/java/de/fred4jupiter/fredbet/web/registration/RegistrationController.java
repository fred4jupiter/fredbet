package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.service.registration.RegistrationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;
import de.fred4jupiter.fredbet.web.matches.CreateEditMatchCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private static final String REGISTRATION_PAGE = "registration/registration";

    @Autowired
    private UserService userService;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private WebSecurityUtil webSecurityUtil;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    public String register(Model model) {
        RegistrationCommand command = new RegistrationCommand();
        model.addAttribute("registrationCommand", command);
        return REGISTRATION_PAGE;
    }

    @PostMapping
    public String registerPost(@Valid RegistrationCommand command, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }

        webMessageUtil.addInfoMsg(redirect, "msg.registration.info.success");
        return "redirect:/";
    }
}
