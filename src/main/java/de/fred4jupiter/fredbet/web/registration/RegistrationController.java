package de.fred4jupiter.fredbet.web.registration;

import de.fred4jupiter.fredbet.service.registration.RegistrationService;
import de.fred4jupiter.fredbet.service.user.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private static final String REGISTRATION_PAGE = "registration/registration";

    private final WebMessageUtil webMessageUtil;

    private final RegistrationService registrationService;

    public RegistrationController(WebMessageUtil webMessageUtil, RegistrationService registrationService) {
        this.webMessageUtil = webMessageUtil;
        this.registrationService = registrationService;
    }

    @GetMapping
    public String register(Model model) {
        if (!registrationService.isSelfRegistrationEnabled()) {
            return "redirect:/";
        }

        RegistrationCommand command = new RegistrationCommand();
        model.addAttribute("registrationCommand", command);
        return REGISTRATION_PAGE;
    }

    @PostMapping
    public String registerPost(@Valid RegistrationCommand command, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return REGISTRATION_PAGE;
        }

        if (!registrationService.isTokenValid(command.getToken())) {
            webMessageUtil.addErrorMsg(model, "msg.registration.error.invalidToken");
            return REGISTRATION_PAGE;
        }

        try {
            registrationService.registerNewUser(command.getUsername(), command.getPassword(), command.isChild());
        } catch (UserAlreadyExistsException e) {
            webMessageUtil.addErrorMsg(model, "user.username.duplicate");
            model.addAttribute("registrationCommand", command);
            return REGISTRATION_PAGE;
        }

        webMessageUtil.addInfoMsg(redirect, "msg.registration.info.success");
        return "redirect:/registration";
    }
}
