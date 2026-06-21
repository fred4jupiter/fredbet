package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.web.bet.BettingWebUtil;
import de.fred4jupiter.fredbet.web.util.TeamUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global model attribute controller for adding utility beans to all templates.
 * This avoids the Thymeleaf bean access restriction in Spring Boot 4.0.
 */
@ControllerAdvice
public class GlobalModelAttributeController {

    private final WebSecurityUtil webSecurityUtil;
    private final RuntimeSettingsUtil runtimeSettingsUtil;
    private final MessageSourceUtil messageSourceUtil;
    private final TeamUtil teamUtil;
    private final GroupAvailabilityUtil groupAvailabilityUtil;
    private final LanguageUtil languageUtil;
    private final BettingWebUtil bettingWebUtil;

    public GlobalModelAttributeController(WebSecurityUtil webSecurityUtil,
                                          RuntimeSettingsUtil runtimeSettingsUtil,
                                          MessageSourceUtil messageSourceUtil,
                                          TeamUtil teamUtil,
                                          GroupAvailabilityUtil groupAvailabilityUtil,
                                          LanguageUtil languageUtil,
                                          BettingWebUtil bettingWebUtil) {
        this.webSecurityUtil = webSecurityUtil;
        this.runtimeSettingsUtil = runtimeSettingsUtil;
        this.messageSourceUtil = messageSourceUtil;
        this.teamUtil = teamUtil;
        this.groupAvailabilityUtil = groupAvailabilityUtil;
        this.languageUtil = languageUtil;
        this.bettingWebUtil = bettingWebUtil;
    }

    @ModelAttribute("webSecurityUtil")
    public WebSecurityUtil webSecurityUtil() {
        return webSecurityUtil;
    }

    @ModelAttribute("runtimeSettingsUtil")
    public RuntimeSettingsUtil runtimeSettingsUtil() {
        return runtimeSettingsUtil;
    }

    @ModelAttribute("messageSourceUtil")
    public MessageSourceUtil messageSourceUtil() {
        return messageSourceUtil;
    }

    @ModelAttribute("teamUtil")
    public TeamUtil teamUtil() {
        return teamUtil;
    }

    @ModelAttribute("groupAvailabilityUtil")
    public GroupAvailabilityUtil groupAvailabilityUtil() {
        return groupAvailabilityUtil;
    }

    @ModelAttribute("languageUtil")
    public LanguageUtil languageUtil() {
        return languageUtil;
    }

    @ModelAttribute("bettingWebUtil")
    public BettingWebUtil bettingWebUtil() {
        return bettingWebUtil;
    }
}
