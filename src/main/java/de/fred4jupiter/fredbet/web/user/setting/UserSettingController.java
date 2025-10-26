package de.fred4jupiter.fredbet.web.user.setting;

import de.fred4jupiter.fredbet.domain.UserSetting;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.user.UserSettingsService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping
    public String showPage(Model model) {
        UserSettingCommand command = new UserSettingCommand(securityService.getCurrentUser().getAppUserSetting().isDarkMode());
        model.addAttribute("userSettingCommand", command);
        return "user/user_settings";
    }

    @PostMapping
    public String save(UserSettingCommand command, RedirectAttributes redirect) {
        userSettingsService.updateUserSetting(securityService.getCurrentUser(), new UserSetting(command.isDarkMode()));
        LOG.debug("save user setting command: {}", command);

        webMessageUtil.addInfoMsg(redirect, "user.setting.updated");
        return "redirect:/user/setting";
    }
}
