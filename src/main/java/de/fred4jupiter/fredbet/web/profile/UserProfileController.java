package de.fred4jupiter.fredbet.web.profile;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.SecurityUtils;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.MessageUtil;
import de.fred4jupiter.fredbet.web.user.ChangePasswordCommand;

@Controller
@RequestMapping("/userprofile")
public class UserProfileController {

    private static final String CHANGE_PASSWORD_PAGE = "userprofile/change_password";

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;
    
    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String changePassword(@ModelAttribute ChangePasswordCommand changePasswordCommand) {
        return CHANGE_PASSWORD_PAGE;
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ModelAndView changePasswordPost(@Valid ChangePasswordCommand changePasswordCommand, RedirectAttributes redirect,
            ModelMap modelMap) {
        if (changePasswordCommand.validate(messageUtil, modelMap)) {
            return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
        }

        if (changePasswordCommand.isPasswordRepeatMismatch()) {
            messageUtil.addPlainErrorMsg(modelMap, "Das neue Passwort stimmt nicht mit der Passwortwiederholung überein!");
            return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
        }

        try {
            AppUser currentUser = SecurityUtils.getCurrentUser();
            userService.changePassword(currentUser.getId(), changePasswordCommand);
        } catch (OldPasswordWrongException e) {
            messageUtil.addPlainErrorMsg(modelMap, "Das alte Passwort ist falsch!");
            return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
        }

        String msg = "Passwort erfolgreich geändert!";
        messageUtil.addPlainInfoMsg(redirect, msg);
        return new ModelAndView("redirect:/matches");
    }
}
