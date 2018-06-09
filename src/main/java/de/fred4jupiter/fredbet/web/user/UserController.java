package de.fred4jupiter.fredbet.web.user;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.UserNotDeletableException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	private static final String EDIT_USER_PAGE = "user/edit";

	private static final String CREATE_USER_PAGE = "user/create";

	@Autowired
	private UserService userService;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private WebSecurityUtil webSecurityUtil;

	@ModelAttribute("availableRoles")
	public List<String> availableRoles() {
		List<FredBetRole> fredBetRoles = Arrays.asList(FredBetRole.values());
		return Collections.unmodifiableList(fredBetRoles.stream().map(Enum::name).collect(Collectors.toList()));
	}

	@RequestMapping
	public ModelAndView list() {
		List<UserDto> users = userService.findAllAsUserDto();
		return new ModelAndView("user/list", "allUsers", users);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") Long userId) {
		AppUser user = userService.findByUserId(userId);

		EditUserCommand editUserCommand = toEditUserCommand(user);

		return new ModelAndView(EDIT_USER_PAGE, "editUserCommand", editUserCommand);
	}

	private EditUserCommand toEditUserCommand(AppUser appUser) {
		EditUserCommand userCommand = new EditUserCommand();
		userCommand.setUserId(appUser.getId());
		userCommand.setUsername(appUser.getUsername());
		userCommand.setDeletable(appUser.isDeletable());
		userCommand.setChild(appUser.isChild());
		if (!CollectionUtils.isEmpty(appUser.getAuthorities())) {
			for (GrantedAuthority grantedAuthority : appUser.getAuthorities()) {
				userCommand.addRole(grantedAuthority.getAuthority());
			}
		}
		return userCommand;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_USER + "')")
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public ModelAndView edit(@Valid EditUserCommand editUserCommand, BindingResult bindingResult, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView(EDIT_USER_PAGE, "editUserCommand", editUserCommand);
		}

		if (webSecurityUtil.isRoleSelectionDisabledForUser(editUserCommand.getUsername())) {
			LOG.debug("Role selection is disabled for user {}. Do not update roles.", editUserCommand.getUsername());
			userService.updateUser(editUserCommand.getUserId(), editUserCommand.isChild());
		} else {
			userService.updateUser(editUserCommand.getUserId(), editUserCommand.getRoles(), editUserCommand.isChild());
		}

		messageUtil.addInfoMsg(redirect, "user.edited", editUserCommand.getUsername());
		return new ModelAndView("redirect:/user");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_USER + "')")
	@RequestMapping("{id}/delete")
	public ModelAndView delete(@PathVariable("id") Long userId, RedirectAttributes redirect) {
		if (securityService.getCurrentUser().getId().equals(userId)) {
			messageUtil.addErrorMsg(redirect, "user.deleted.couldNotDeleteOwnUser");
			return new ModelAndView("redirect:/user");
		}

		AppUser appUser = userService.findByUserId(userId);
		try {
			userService.deleteUser(userId);
			messageUtil.addInfoMsg(redirect, "user.deleted", appUser.getUsername());
		} catch (UserNotDeletableException e) {
			messageUtil.addErrorMsg(redirect, "user.not.deletable", appUser.getUsername());
		}

		return new ModelAndView("redirect:/user");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_USER + "')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		return new ModelAndView(CREATE_USER_PAGE, "createUserCommand", new CreateUserCommand());
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_USER + "')")
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid CreateUserCommand createUserCommand, BindingResult bindingResult, RedirectAttributes redirect,
			ModelMap modelMap) {
		if (bindingResult.hasErrors()) {
			return new ModelAndView(CREATE_USER_PAGE, "createUserCommand", createUserCommand);
		}

		try {
			// create new user
			AppUserBuilder appUserBuilder = AppUserBuilder.create()
					.withUsernameAndPassword(createUserCommand.getUsername(), createUserCommand.getPassword())
					.withIsChild(createUserCommand.isChild()).withFirstLogin(true);

			if (webSecurityUtil.isRoleSelectionDisabledForUser(createUserCommand.getUsername())) {
				LOG.debug("Role selection is disabled for user {}. Using default role {}", createUserCommand.getUsername(),
						FredBetRole.ROLE_USER);
				appUserBuilder.withRole(FredBetRole.ROLE_USER);
			} else {
				appUserBuilder.withRoles(createUserCommand.getRoles());
			}

			userService.createUser(appUserBuilder.build());
		} catch (UserAlreadyExistsException e) {
			messageUtil.addErrorMsg(modelMap, "user.username.duplicate");
			return new ModelAndView(CREATE_USER_PAGE, "createUserCommand", createUserCommand);
		}

		messageUtil.addInfoMsg(redirect, "user.created", createUserCommand.getUsername());
		return new ModelAndView("redirect:/user");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_PASSWORD_RESET + "')")
	@RequestMapping("{id}/resetPwd")
	public ModelAndView resetPassword(@PathVariable("id") Long userId, RedirectAttributes redirect) {
		String username = userService.resetPasswordForUser(userId);
		messageUtil.addInfoMsg(redirect, "user.password.reset", username);
		return new ModelAndView("redirect:/user");
	}
}
