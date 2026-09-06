package de.fred4jupiter.fredbet.country;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.ResourceToPropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class CountryResolver {

    private final Properties countryAlpha2Codes;

    public CountryResolver(@Value("classpath:/country-alpha2.properties") Resource countryAlpha2Codes) {
        this.countryAlpha2Codes = ResourceToPropertiesUtil.loadCountryNames(countryAlpha2Codes);
    }

    public String countryToAlpha2Code(Country country) {
        if (country == null) {
            return "";
        }

        return countryAlpha2Codes.getProperty(country.name());
    }
}
