package de.fred4jupiter.fredbet;

import com.neovisionaries.i18n.CountryCode;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.TempFileWriterUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CountryResolveTest {

    private static final Logger LOG = LoggerFactory.getLogger(CountryResolveTest.class);

    private final Map<Country, String> alpha2Codes = new HashMap<>();

    @Test
    void resolve() {
        List<Country> countryList = Stream.of(Country.values()).toList();
        countryList.forEach(country -> {
            this.alpha2Codes.put(country, resolveFor(country));
        });

        StringBuilder sb = new StringBuilder();

        alpha2Codes.forEach((country, alpha2) -> {
            sb.append(country.name()).append("=").append(alpha2).append("\n");
        });

        TempFileWriterUtil.writeToTempFolder(sb.toString().getBytes(), "country-alpha2.properties");
    }

    private String resolveFor(Country country) {
        if (StringUtils.isNotBlank(country.getFlagIconCode())) {
            return country.getFlagIconCode();
        }

        if (StringUtils.isNotBlank(country.getAlpha3Code())) {
            String alpha3 = country.getAlpha3Code().toUpperCase();
            CountryCode countryCode = CountryCode.getByAlpha3Code(alpha3);
            if (countryCode != null && countryCode.getAlpha2() != null) {
                return countryCode.getAlpha2();
            }
            LOG.warn("missing alpha 2 code for country={}, alpha3={}", country, alpha3);
        }

        return null;
    }
}
