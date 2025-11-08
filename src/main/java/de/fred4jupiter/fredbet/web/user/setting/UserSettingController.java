package de.fred4jupiter.fredbet.web.user.setting;

import de.fred4jupiter.fredbet.domain.BootswatchTheme;
import de.fred4jupiter.fredbet.domain.NavbarLayout;
import de.fred4jupiter.fredbet.domain.UserSetting;
import de.fred4jupiter.fredbet.domain.entity.AppUserSetting;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.user.UserSettingsService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/user/setting")
public class UserSettingController {

    private static final Logger LOG = LoggerFactory.getLogger(UserSettingController.class);

    private final WebMessageUtil webMessageUtil;

    private final UserSettingsService userSettingsService;

    private final SecurityService securityService;

    public UserSettingController(WebMessageUtil webMessageUtil, UserSettingsService userSettingsService,
                                 SecurityService securityService) {
        this.webMessageUtil = webMessageUtil;
        this.userSettingsService = userSettingsService;
        this.securityService = securityService;
    }

    @ModelAttribute("availableThemes")
    public List<BootswatchTheme> availableThemes() {
        return Arrays.asList(BootswatchTheme.values());
    }

    @ModelAttribute("availableNavbarLayouts")
    public List<NavbarLayout> availableNavbarLayouts() {
        return Arrays.asList(NavbarLayout.values());
    }

    @GetMapping
    public String showPage(Model model) {
        AppUserSetting appUserSetting = securityService.getCurrentUser().getAppUserSetting();
        UserSettingCommand command = new UserSettingCommand();
        command.setDarkMode(appUserSetting.isDarkMode());
        command.setBootswatchTheme(appUserSetting.getBootswatchTheme());
        command.setNavbarLayout(appUserSetting.getNavbarLayout());

        model.addAttribute("userSettingCommand", command);
        return "user/user_settings";
    }

    @PostMapping
    public String save(UserSettingCommand command, RedirectAttributes redirect) {
        UserSetting userSetting = new UserSetting(command.isDarkMode(), command.getBootswatchTheme(), command.getNavbarLayout());

        userSettingsService.updateUserSetting(securityService.getCurrentUser(), userSetting);
        LOG.debug("save user setting command: {}", command);

        webMessageUtil.addInfoMsg(redirect, "user.setting.updated");
        return "redirect:/user/setting";
    }
}
