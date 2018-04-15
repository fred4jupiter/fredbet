package de.fred4jupiter.fredbet.web.profile;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

	private static final String CHANGE_PASSWORD_PAGE = "profile/change_password";

	private static final String CHANGE_USERNAME_PAGE = "profile/change_username";

	@Autowired
	private UserService userService;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private WebSecurityUtil webSecurityUtil;

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String changePassword(@ModelAttribute ChangePasswordCommand changePasswordCommand, ModelMap modelMap) {
		if (webSecurityUtil.isUsersFirstLogin()) {
			messageUtil.addWarnMsg(modelMap, "user.changePassword.firstLogin");
		}

		return CHANGE_PASSWORD_PAGE;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ModelAndView changePasswordPost(@Valid ChangePasswordCommand changePasswordCommand, BindingResult bindingResult,
			RedirectAttributes redirect, ModelMap modelMap) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
		}

		try {
			AppUser currentUser = securityService.getCurrentUser();
			userService.changePassword(currentUser.getId(), changePasswordCommand.getOldPassword(), changePasswordCommand.getNewPassword());
		} catch (OldPasswordWrongException e) {
			messageUtil.addErrorMsg(modelMap, "msg.bet.betting.error.oldPasswordWrong");
			return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
		}

		messageUtil.addInfoMsg(redirect, "msg.user.profile.info.passwordChanged");
		return new ModelAndView("redirect:/matches");
	}

	@RequestMapping(value = "/changeUsername", method = RequestMethod.GET)
	public String changeUsername(@ModelAttribute ChangeUsernameCommand changeUsernameCommand) {
		return CHANGE_USERNAME_PAGE;
	}

	@RequestMapping(value = "/changeUsername", method = RequestMethod.POST)
	public ModelAndView changeUsernamePost(@Valid ChangeUsernameCommand changeUsernameCommand, BindingResult bindingResult,
			RedirectAttributes redirect, ModelMap modelMap) {

		if (bindingResult.hasErrors()) {
			return new ModelAndView(CHANGE_USERNAME_PAGE, "changeUsernameCommand", changeUsernameCommand);
		}

		try {
			userService.renameUser(securityService.getCurrentUserName(), changeUsernameCommand.getNewUsername());
		} catch (UserAlreadyExistsException e) {
			messageUtil.addErrorMsg(modelMap, "user.username.duplicate");
			return new ModelAndView("profile/change_username", "changeUsernameCommand", changeUsernameCommand);
		}

		messageUtil.addInfoMsg(redirect, "msg.user.profile.info.usernameChanged");

		return new ModelAndView("redirect:/profile/changeUsername");
	}
}
