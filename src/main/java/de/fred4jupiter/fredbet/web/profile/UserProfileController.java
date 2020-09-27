package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.RenameUsernameNotAllowedException;
import de.fred4jupiter.fredbet.service.user.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final String CHANGE_PASSWORD_PAGE = "profile/change_password";

    private static final String CHANGE_USERNAME_PAGE = "profile/change_username";

    private final UserService userService;

    private final WebMessageUtil webMessageUtil;

    private final SecurityService securityService;

    private final WebSecurityUtil webSecurityUtil;

    public UserProfileController(UserService userService, WebMessageUtil webMessageUtil, SecurityService securityService,
                                 WebSecurityUtil webSecurityUtil) {
        this.userService = userService;
        this.webMessageUtil = webMessageUtil;
        this.securityService = securityService;
        this.webSecurityUtil = webSecurityUtil;
    }

    @GetMapping("/changePassword")
    public String changePassword(@ModelAttribute ChangePasswordCommand changePasswordCommand, Model model) {
        if (webSecurityUtil.isUsersFirstLogin()) {
            webMessageUtil.addWarnMsg(model, "user.changePassword.firstLogin");
        }

        return CHANGE_PASSWORD_PAGE;
    }

    @PostMapping("/changePassword")
    public String changePasswordPost(@Valid ChangePasswordCommand changePasswordCommand, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return CHANGE_PASSWORD_PAGE;
        }

        try {
            AppUser currentUser = securityService.getCurrentUser();
            userService.changePassword(currentUser.getId(), changePasswordCommand.getOldPassword(), changePasswordCommand.getNewPassword());
        } catch (OldPasswordWrongException e) {
            webMessageUtil.addErrorMsg(model, "msg.bet.betting.error.oldPasswordWrong");
            model.addAttribute("changePasswordCommand", changePasswordCommand);
            return CHANGE_PASSWORD_PAGE;
        }

        webMessageUtil.addInfoMsg(redirect, "msg.user.profile.info.passwordChanged");
        return "redirect:/matches";
    }

    @GetMapping("/changeUsername")
    public String changeUsername(@ModelAttribute ChangeUsernameCommand changeUsernameCommand) {
        return CHANGE_USERNAME_PAGE;
    }

    @PostMapping("/changeUsername")
    public String changeUsernamePost(@Valid ChangeUsernameCommand changeUsernameCommand, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return CHANGE_USERNAME_PAGE;
        }

        try {
            userService.renameUser(securityService.getCurrentUserName(), changeUsernameCommand.getNewUsername());
        } catch (UserAlreadyExistsException e) {
            webMessageUtil.addErrorMsg(model, "user.username.duplicate");
            model.addAttribute("changeUsernameCommand", changeUsernameCommand);
            return "profile/change_username";
        } catch (RenameUsernameNotAllowedException e) {
            webMessageUtil.addErrorMsg(model, "user.username.changeNotAllowed");
            model.addAttribute("changeUsernameCommand", changeUsernameCommand);
            return "profile/change_username";
        }

        webMessageUtil.addInfoMsg(redirect, "msg.user.profile.info.usernameChanged");

        return "redirect:/profile/changeUsername";
    }
}
