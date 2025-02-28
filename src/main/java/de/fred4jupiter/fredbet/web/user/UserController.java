package de.fred4jupiter.fredbet.web.user;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.user.UserAdministrationService;
import de.fred4jupiter.fredbet.user.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.user.UserNotDeletableException;
import de.fred4jupiter.fredbet.user.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static final String EDIT_USER_PAGE = "user/edit";

    private static final String CREATE_USER_PAGE = "user/create";

    private static final String LIST_USER_PAGE = "user/list";

    private static final String REDIRECT_USER_PAGE = "redirect:/user";

    private final UserService userService;

    private final WebMessageUtil webMessageUtil;

    private final WebSecurityUtil webSecurityUtil;

    private final FredbetProperties fredbetProperties;

    private final UserAdministrationService userAdministrationService;

    public UserController(UserService userService, WebMessageUtil webMessageUtil, WebSecurityUtil webSecurityUtil,
                          FredbetProperties fredbetProperties, UserAdministrationService userAdministrationService) {
        this.userService = userService;
        this.webMessageUtil = webMessageUtil;
        this.webSecurityUtil = webSecurityUtil;
        this.fredbetProperties = fredbetProperties;
        this.userAdministrationService = userAdministrationService;
    }

    @ModelAttribute("availableRoles")
    public List<String> availableRoles() {
        List<FredBetUserGroup> fredBetUserGroups = Arrays.asList(FredBetUserGroup.values());
        return fredBetUserGroups.stream().map(Enum::name).toList();
    }

    @GetMapping
    public String list(Model model) {
        if (webSecurityUtil.isCurrentUserTheDefaultAdminUser()) {
            // show also the admin user in overview
            List<UserDto> users = userService.findAllAsUserDto(true);
            model.addAttribute("allUsers", users);
        } else {
            // don´t show admin user in overview
            List<UserDto> users = userService.findAllAsUserDto(false);
            model.addAttribute("allUsers", users);
        }

        return LIST_USER_PAGE;
    }

    @GetMapping("{id}")
    public String edit(@PathVariable("id") Long userId, Model model, RedirectAttributes redirect) {
        AppUser user = userService.findByUserId(userId);
        if (!webSecurityUtil.isCurrentUserTheDefaultAdminUser() && fredbetProperties.adminUsername().equals(user.getUsername())) {
            // other admin tries to update the default admin user
            webMessageUtil.addErrorMsg(redirect, "user.edited.notAllowed");
            return REDIRECT_USER_PAGE;
        }

        EditUserCommand editUserCommand = toEditUserCommand(user);

        model.addAttribute("editUserCommand", editUserCommand);
        return EDIT_USER_PAGE;
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
    @PostMapping("/edit")
    public String edit(@Valid EditUserCommand editUserCommand, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return EDIT_USER_PAGE;
        }

        if (webSecurityUtil.isRoleSelectionDisabledForUser(editUserCommand.getUsername())) {
            LOG.debug("Role selection is disabled for user {}. Do not update roles.", editUserCommand.getUsername());
            userService.updateUser(editUserCommand.getUserId(), editUserCommand.isChild());
        } else {
            userService.updateUser(editUserCommand.getUserId(), editUserCommand.getRoles(), editUserCommand.isChild());
        }

        webMessageUtil.addInfoMsg(redirect, "user.edited", editUserCommand.getUsername());
        return REDIRECT_USER_PAGE;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_USER + "')")
    @GetMapping("{id}/delete")
    public String delete(@PathVariable("id") Long userId, RedirectAttributes redirect, @AuthenticationPrincipal AppUser currentUser) {
        if (currentUser.getId().equals(userId)) {
            webMessageUtil.addErrorMsg(redirect, "user.deleted.couldNotDeleteOwnUser");
            return REDIRECT_USER_PAGE;
        }

        AppUser userToBeDeleted = userService.findByUserId(userId);
        try {
            userService.deleteUser(userId);
            webMessageUtil.addInfoMsg(redirect, "user.deleted", userToBeDeleted.getUsername());
        } catch (UserNotDeletableException e) {
            webMessageUtil.addErrorMsg(redirect, "user.not.deletable", userToBeDeleted.getUsername());
        }

        return REDIRECT_USER_PAGE;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_USER + "')")
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("createUserCommand", new CreateUserCommand());
        return CREATE_USER_PAGE;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_CREATE_USER + "')")
    @PostMapping
    public String create(@Valid CreateUserCommand createUserCommand, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
        if (bindingResult.hasErrors()) {
            return CREATE_USER_PAGE;
        }

        try {
            // create new user
            AppUserBuilder appUserBuilder = AppUserBuilder.create()
                    .withUsernameAndPassword(createUserCommand.getUsername(), createUserCommand.getPassword())
                    .withIsChild(createUserCommand.isChild()).withFirstLogin(true);

            if (webSecurityUtil.isRoleSelectionDisabledForUser(createUserCommand.getUsername())) {
                LOG.debug("Role selection is disabled for user {}. Using default role {}", createUserCommand.getUsername(),
                        FredBetUserGroup.ROLE_USER);
                appUserBuilder.withUserGroup(FredBetUserGroup.ROLE_USER);
            } else {
                appUserBuilder.withUserGroups(createUserCommand.getRoles());
            }

            userService.createUser(appUserBuilder.build());
        } catch (UserAlreadyExistsException e) {
            webMessageUtil.addErrorMsg(model, "user.username.duplicate");
            model.addAttribute("createUserCommand", createUserCommand);
            return CREATE_USER_PAGE;
        }

        webMessageUtil.addInfoMsg(redirect, "user.created", createUserCommand.getUsername());
        return REDIRECT_USER_PAGE;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_PASSWORD_RESET + "')")
    @GetMapping("{id}/resetPwd")
    public String resetPassword(@PathVariable("id") Long userId, RedirectAttributes redirect) {
        String username = userAdministrationService.resetPasswordForUser(userId);
        webMessageUtil.addInfoMsg(redirect, "user.password.reset", username);
        return REDIRECT_USER_PAGE;
    }
}
