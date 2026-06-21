package de.fred4jupiter.fredbet.web.language;

public record LanguageSupport(String languageCode, String flagCode) {

    public LanguageSupport(String languageCode) {
        this(languageCode, languageCode);
    }
}
