package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class CountryResolvingIT {

    @Autowired
    private MessageSource messageSource;

    @Test
    void resolveAllCountryNames() {
        List<Country> countryList = Stream.of(Country.values()).filter(country -> !Country.NONE.equals(country)).toList();

        countryList.forEach(country -> {
            String countryName = resolve(country);
            assertThat(countryName).isNotBlank();
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
