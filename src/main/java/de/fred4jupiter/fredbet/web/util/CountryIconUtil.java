package de.fred4jupiter.fredbet.web.util;

import com.neovisionaries.i18n.CountryCode;
import de.fred4jupiter.fredbet.domain.Country;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class CountryIconUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CountryIconUtil.class);

    private final Map<Country, String> alpha2Codes = new HashMap<>();

    public CountryIconUtil() {
        List<Country> countryList = Stream.of(Country.values()).filter(country -> !Country.NONE.equals(country)).toList();
        countryList.forEach(country -> {
            this.alpha2Codes.put(country, resolveFor(country));
        });
    }

    private String resolveFor(Country country) {
        if (StringUtils.isNotBlank(country.getFlagIconCode())) {
            return country.getFlagIconCode();
        }

        String alpha3 = country.getAlpha3Code().toUpperCase();
        CountryCode countryCode = CountryCode.getByAlpha3Code(alpha3);
        if (countryCode != null && countryCode.getAlpha2() != null) {
            return countryCode.getAlpha2();
        }

        LOG.error("missing alpha 2 code for country={}, alpha3={}", country, alpha3);
        return null;
    }

    public String cssClassFor(Country country) {
        if (country == null) {
            LOG.warn("Given country is null. Could not resolve css class for icon flag!");
            return "";
        }

        return cssClassFor(this.alpha2Codes.get(country));
    }

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
        return cssClassFor(language);
    }
}
