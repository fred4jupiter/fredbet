package de.fred4jupiter.fredbet.domain;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryMapper {

    private static final CountryMapper INSTANCE = new CountryMapper();

    private Map<String, Locale> localeMap;

    private CountryMapper() {
        initCountryCodeMapping();
    }

    public static CountryMapper getInstance() {
        return INSTANCE;
    }

    private void initCountryCodeMapping() {
        String[] countries = Locale.getISOCountries();
        this.localeMap = new HashMap<>(countries.length);
        for (String country : countries) {
            Locale locale = Locale.of("", country);
            this.localeMap.put(locale.getISO3Country(), locale);
        }
    }

    public String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
        return localeMap.get(iso3CountryCode).getCountry();
    }

    public String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
        Locale locale = Locale.of("", iso2CountryCode);
        return locale.getISO3Country();
    }
}
