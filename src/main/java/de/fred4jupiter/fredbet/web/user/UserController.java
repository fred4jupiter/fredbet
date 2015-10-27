package de.fred4jupiter.fredbet.web.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.SecurityUtils;
import de.fred4jupiter.fredbet.service.OldPasswordWrongException;
import de.fred4jupiter.fredbet.service.UserNotDeletableException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private MessageUtil messageUtil;

	@RequestMapping
	public ModelAndView list() {
		List<AppUser> users = userService.findAll();
		return new ModelAndView("user/list", "allUsers", users);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String userId) {
		UserCommand userCommand = userService.findByUserId(userId);
		return new ModelAndView("user/form", "userCommand", userCommand);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping("{id}/delete")
	public ModelAndView delete(@PathVariable("id") String userId, RedirectAttributes redirect) {
		UserCommand userCommand = userService.findByUserId(userId);

		if (SecurityUtils.getCurrentUser().getId().equals(userId)) {
			messageUtil.addPlainErrorMsg(redirect, "Der eigene Benutzer kann nicht gelöscht werden!");
			return new ModelAndView("redirect:/user");
		}

		try {
			userService.deleteUser(userId);
			messageUtil.addInfoMsg(redirect, "user.deleted",userCommand.getUsername());
		} catch (UserNotDeletableException e) {
			messageUtil.addErrorMsg(redirect, "user.not.deletable", userCommand.getUsername());
		}

		return new ModelAndView("redirect:/user");
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createForm(@ModelAttribute UserCommand userCommand) {
		return "user/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid UserCommand userCommand, RedirectAttributes redirect, ModelMap modelMap) {
		if (userCommand.validate(messageUtil, modelMap)) {
			return new ModelAndView("user/form", "userCommand", userCommand);
		}

		try {
			userService.createOrUpdateUser(userCommand);
		} catch (DuplicateKeyException e) {
			messageUtil.addPlainErrorMsg(modelMap, "Dieser Benutzername ist bereits vergeben!");
			return new ModelAndView("user/form", "userCommand", userCommand);
		}

		String msg = "Benutzer " + userCommand.getUsername() + " angelegt/aktualisiert!";
		messageUtil.addPlainInfoMsg(redirect, msg);
		return new ModelAndView("redirect:/user");
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String changePassword(@ModelAttribute ChangePasswordCommand changePasswordCommand) {
		return "user/change_password";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ModelAndView changePasswordPost(@Valid ChangePasswordCommand changePasswordCommand, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (changePasswordCommand.validate(messageUtil, modelMap)) {
			return new ModelAndView("user/change_password", "changePasswordCommand", changePasswordCommand);
		}

		if (changePasswordCommand.isPasswordRepeatMismatch()) {
			messageUtil.addPlainErrorMsg(modelMap, "Das neue Passwort stimmt nicht mit der Passwortwiederholung überein!");
			return new ModelAndView("user/change_password", "changePasswordCommand", changePasswordCommand);
		}

		try {
			userService.changePassword(SecurityUtils.getCurrentUser(), changePasswordCommand);
		} catch (OldPasswordWrongException e) {
			messageUtil.addPlainErrorMsg(modelMap, "Das alte Passwort ist falsch!");
			return new ModelAndView("user/change_password", "changePasswordCommand", changePasswordCommand);
		}

		String msg = "Passwort erfolgreich geändert!";
		messageUtil.addPlainInfoMsg(redirect, msg);
		return new ModelAndView("redirect:/matches");
	}

}
