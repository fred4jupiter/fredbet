package de.fred4jupiter.fredbet.web.util;

import com.neovisionaries.i18n.CountryCode;
import de.fred4jupiter.fredbet.domain.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryIconUtil {

    public String cssClassFor(Country country) {
        CountryCode countryCode = CountryCode.getByAlpha3Code(country.getIsoCode().toUpperCase());
        if (countryCode == null) {
            return "";
        }
        String alpha2 = countryCode.getAlpha2();

        return "fi-%s".formatted(alpha2.toLowerCase());
    }
}
