package de.fred4jupiter.fredbet.domain;

import java.util.Locale;

public interface TranslationAware {

    String getCountryName(Country country, Locale locale);
}
