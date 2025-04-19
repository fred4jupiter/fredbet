package de.fred4jupiter.fredbet.web.util;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LanguageIconUtil {

    public String cssClassFor(String alpha2Code) {
        return "fi-%s".formatted(alpha2Code.toLowerCase());
    }

    public String cssClassCurrentUserLanguage() {
        String language = LocaleContextHolder.getLocale().getLanguage();
        if ("en".equals(language)) {
            return cssClassFor("gb");
        }
        if ("cs".equals(language)) {
            return cssClassFor("cz");
        }
        if ("ca".equals(language)) {
            return cssClassFor("es-ct");
        }
        return cssClassFor(language);
    }
}
