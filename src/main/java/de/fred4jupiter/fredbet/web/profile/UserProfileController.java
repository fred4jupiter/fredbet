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
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

	private static final String CHANGE_PASSWORD_PAGE = "profile/change_password";

	@Autowired
	private UserService userService;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private SecurityService securityService;

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String changePassword(@ModelAttribute ChangePasswordCommand changePasswordCommand) {
		return CHANGE_PASSWORD_PAGE;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ModelAndView changePasswordPost(@Valid ChangePasswordCommand changePasswordCommand,
			RedirectAttributes redirect, ModelMap modelMap) {
		if (changePasswordCommand.validate(messageUtil, modelMap)) {
			return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
		}

		if (changePasswordCommand.isPasswordRepeatMismatch()) {
			messageUtil.addErrorMsg(modelMap, "msg.bet.betting.error.passwordMismatch");
			return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
		}

		try {
			AppUser currentUser = securityService.getCurrentUser();
			userService.changePassword(currentUser.getId(), changePasswordCommand);
		} catch (OldPasswordWrongException e) {
			messageUtil.addErrorMsg(modelMap, "msg.bet.betting.error.oldPasswordWrong");
			return new ModelAndView(CHANGE_PASSWORD_PAGE, "changePasswordCommand", changePasswordCommand);
		}

		messageUtil.addInfoMsg(redirect, "msg.user.profile.info.passwordChanged");
		return new ModelAndView("redirect:/matches");
	}
}
