package de.fred4jupiter.fredbet.web;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Country;

@Component
public class LanguageUtil {

	private static final String LANGUAGE_ICON_BASE_PATH = "/images/lang/";

	public String getCurrentUserLanguage() {
		return LocaleContextHolder.getLocale().getLanguage();
	}

	public String getImageLanguageIconPath() {
		return LANGUAGE_ICON_BASE_PATH + getCurrentUserLanguage() + ".png";
	}
}
