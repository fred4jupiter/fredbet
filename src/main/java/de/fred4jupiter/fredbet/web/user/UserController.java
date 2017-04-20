package de.fred4jupiter.fredbet.web.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.UserNotDeletableException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final String EDIT_USER_PAGE = "user/edit";

	private static final String CREATE_USER_PAGE = "user/create";

	@Autowired
	private UserService userService;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private SecurityService securityService;

	@RequestMapping
	public ModelAndView list() {
		List<UserDto> users = userService.findAllAsUserDto();
		return new ModelAndView("user/list", "allUsers", users);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") Long userId) {
		UserCommand userCommand = userService.findByUserId(userId);
		return new ModelAndView(EDIT_USER_PAGE, "userCommand", userCommand);
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_USER + "')")
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public ModelAndView edit(@Valid UserCommand userCommand, RedirectAttributes redirect, ModelMap modelMap) {
		if (Validator.isEmpty(userCommand.getRoles())) {
			messageUtil.addErrorMsg(modelMap, "user.edited.noRole");
			return new ModelAndView(EDIT_USER_PAGE, "userCommand", userCommand);
		}

		AppUser updateUser = userService.updateUser(userCommand);
		messageUtil.addInfoMsg(redirect, "user.edited", updateUser.getUsername());
		return new ModelAndView("redirect:/user");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_USER + "')")
	@RequestMapping("{id}/delete")
	public ModelAndView delete(@PathVariable("id") Long userId, RedirectAttributes redirect) {
		UserCommand userCommand = userService.findByUserId(userId);

		if (securityService.getCurrentUser().getId().equals(userId)) {
			messageUtil.addErrorMsg(redirect, "user.deleted.couldNotDeleteOwnUser");
			return new ModelAndView("redirect:/user");
		}

		try {
			userService.deleteUser(userId);
			messageUtil.addInfoMsg(redirect, "user.deleted", userCommand.getUsername());
		} catch (UserNotDeletableException e) {
			messageUtil.addErrorMsg(redirect, "user.not.deletable", userCommand.getUsername());
		}

		return new ModelAndView("redirect:/user");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_USER + "')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(@ModelAttribute UserCommand userCommand) {
		return CREATE_USER_PAGE;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_USER + "')")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid UserCommand userCommand, RedirectAttributes redirect, ModelMap modelMap) {
		if (userCommand.validate(messageUtil, modelMap)) {
			return new ModelAndView(CREATE_USER_PAGE, "userCommand", userCommand);
		}

		try {
			userService.createUser(userCommand);
		} catch (UserAlreadyExistsException e) {
			messageUtil.addErrorMsg(modelMap, "user.username.duplicate");
			return new ModelAndView(CREATE_USER_PAGE, "userCommand", userCommand);
		}

		messageUtil.addInfoMsg(redirect, "user.created", userCommand.getUsername());
		return new ModelAndView("redirect:/user");
	}

}
