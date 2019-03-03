package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Helper class for preparing messages.
 *
 * @author michael
 */
@Component
public class WebMessageUtil {

    private static final String MSG_ATTRIBUTE_NAME = "globalMessage";

    private static final String CSS_ALERT_ERROR = "alert-danger";

    private static final String CSS_ALERT_WARN = "alert-warning";

    private static final String CSS_ALT_SUCCESS = "alert-success";

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    public void addInfoMsg(RedirectAttributes redirect, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        addMessage(redirect, CSS_ALT_SUCCESS, message);
    }

    public void addInfoMsg(ModelMap modelMap, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        modelMap.addAttribute(MSG_ATTRIBUTE_NAME, new WebMessage(CSS_ALT_SUCCESS, message));
    }

    public void addInfoMsg(Model model, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        model.addAttribute(MSG_ATTRIBUTE_NAME, new WebMessage(CSS_ALT_SUCCESS, message));
    }

    public void addErrorMsg(RedirectAttributes redirect, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        addMessage(redirect, CSS_ALERT_ERROR, message);
    }

    public void addErrorMsg(ModelMap modelMap, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        modelMap.addAttribute(MSG_ATTRIBUTE_NAME, new WebMessage(CSS_ALERT_ERROR, message));
    }

    public void addWarnMsg(RedirectAttributes redirect, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        addMessage(redirect, CSS_ALERT_WARN, message);
    }

    public void addWarnMsg(ModelMap modelMap, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        modelMap.addAttribute(MSG_ATTRIBUTE_NAME, new WebMessage(CSS_ALERT_WARN, message));
    }

    public void addWarnMsg(Model model, String msgKey, Object... params) {
        String message = getMessageFor(msgKey, params);
        model.addAttribute(MSG_ATTRIBUTE_NAME, new WebMessage(CSS_ALERT_WARN, message));
    }

    private void addMessage(RedirectAttributes redirect, String cssClass, String text) {
        redirect.addFlashAttribute(MSG_ATTRIBUTE_NAME, new WebMessage(cssClass, text));
    }

    public String getMessageFor(String msgKey, Object... params) {
        return messageSourceUtil.getMessageFor(msgKey, LocaleContextHolder.getLocale(), params);
    }

    public String getCountryName(Country country) {
        return messageSourceUtil.getCountryName(country, LocaleContextHolder.getLocale());
    }

    public String getTeamNameOne(Match match) {
        return getTeamName(match.getCountryOne(), match.getTeamNameOne());
    }

    public String getTeamNameTwo(Match match) {
        return getTeamName(match.getCountryTwo(), match.getTeamNameTwo());
    }

    public String getTeamName(Country country, String teamName) {
        if (country == null) {
            return teamName;
        }
        return Validator.isNotNull(country) ? getCountryName(country) : teamName;
    }



    public static final class WebMessage {
        private String cssClass;

        private String text;

        public WebMessage(String cssClass, String text) {
            super();
            this.cssClass = cssClass;
            this.text = text;
        }

        public String getCssClass() {
            return cssClass;
        }

        public String getText() {
            return text;
        }
    }
}
