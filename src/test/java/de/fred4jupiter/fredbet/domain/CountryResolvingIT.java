package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class CountryResolvingIT {

    private static final Logger LOG = LoggerFactory.getLogger(CountryResolvingIT.class);

    @Autowired
    private MessageSource messageSource;

    @Test
    void resolveAllCountryNames() {
        List<Country> countryList = Stream.of(Country.values()).filter(country -> !Country.NONE.equals(country)).toList();

        countryList.forEach(country -> {
            String countryName = resolve(country);
            if (StringUtils.isBlank(countryName)) {
                LOG.error("country={} has code={}", country, country.getAlpha3Code());
            }
        });
    }

    private String resolve(Country country) {
        try {
            String countryName = messageSource.getMessage("country." + country.getAlpha3Code(), null, Locale.GERMAN);
            assertThat(countryName).isNotBlank();
            return countryName;
        } catch (NoSuchMessageException e) {
            return null;
        }
    }
}
