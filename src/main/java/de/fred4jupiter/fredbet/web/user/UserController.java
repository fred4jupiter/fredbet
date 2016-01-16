package de.fred4jupiter.fredbet.web.user;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
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

import de.fred4jupiter.fredbet.FredBetPermission;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.SecurityUtils;
import de.fred4jupiter.fredbet.service.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.UserNotDeletableException;
import de.fred4jupiter.fredbet.service.UserService;
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

    @RequestMapping
    public ModelAndView list() {
        List<AppUser> users = userService.findAll();
        return new ModelAndView("user/list", "allUsers", users);
    }

    @RequestMapping("{id}")
    public ModelAndView edit(@PathVariable("id") String userId) {
        UserCommand userCommand = userService.findByUserId(userId);
        return new ModelAndView(EDIT_USER_PAGE, "userCommand", userCommand);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ModelAndView edit(@Valid UserCommand userCommand, RedirectAttributes redirect, ModelMap modelMap) {
        if (CollectionUtils.isEmpty(userCommand.getRoles())) {
            messageUtil.addPlainErrorMsg(modelMap, "Bitte wählen Sie mind. eine Berechtigung!");
            return new ModelAndView(EDIT_USER_PAGE, "userCommand", userCommand);
        }

        AppUser updateUser = userService.updateUser(userCommand);

        String msg = "Benutzer " + updateUser.getUsername() + " aktualisiert!";
        messageUtil.addPlainInfoMsg(redirect, msg);
        return new ModelAndView("redirect:/user");
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_DELETE_USER + "')")
    @RequestMapping("{id}/delete")
    public ModelAndView delete(@PathVariable("id") String userId, RedirectAttributes redirect) {
        UserCommand userCommand = userService.findByUserId(userId);

        if (SecurityUtils.getCurrentUser().getId().equals(userId)) {
            messageUtil.addPlainErrorMsg(redirect, "Der eigene Benutzer kann nicht gelöscht werden!");
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
            messageUtil.addPlainErrorMsg(modelMap, "Dieser Benutzername ist bereits vergeben!");
            return new ModelAndView(CREATE_USER_PAGE, "userCommand", userCommand);
        }

        String msg = "Benutzer " + userCommand.getUsername() + " angelegt!";
        messageUtil.addPlainInfoMsg(redirect, msg);
        return new ModelAndView("redirect:/user");
    }

}
