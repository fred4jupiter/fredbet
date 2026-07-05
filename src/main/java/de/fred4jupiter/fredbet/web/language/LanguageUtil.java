package de.fred4jupiter.fredbet.web.language;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Component
public class LanguageUtil {

    public static final List<LanguageSupport> SUPPORTED_LANGUAGES = List.of(
        new LanguageSupport("de"),
        new LanguageSupport("en", "gb"),
        new LanguageSupport("nl"),
        new LanguageSupport("fr"),
        new LanguageSupport("es"),
        new LanguageSupport("ca", "es-ct"),
        new LanguageSupport("pl"),
        new LanguageSupport("cs", "cz"),
        new LanguageSupport("sv", "se"),
        new LanguageSupport("et", "ee")
    );

    public String buildUrl(String languageCode) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        return builder.replaceQueryParam("lang", languageCode).toUriString();
    }

    public String cssClassFor(String alpha2Code) {
        return "fi-%s".formatted(alpha2Code.toLowerCase());
    }

    public String cssClassCurrentUserLanguage() {
        String language = LocaleContextHolder.getLocale().getLanguage();

        return SUPPORTED_LANGUAGES.stream()
            .filter(sl -> sl.languageCode().equals(language))
            .map(ls -> cssClassFor(ls.flagCode()))
            .findFirst().
            orElse(cssClassFor("de"));
    }
}
