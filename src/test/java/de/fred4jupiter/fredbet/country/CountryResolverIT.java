package de.fred4jupiter.fredbet.country;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class CountryResolverIT {

    @Autowired
    private CountryResolver countryResolver;

    @Test
    void resolveCountryGermany() {
        assertThat(countryResolver.countryToAlpha2Code(Country.GERMANY)).isEqualTo("DE");
    }

    @Test
    void resolveCountries() {
        List<Country> countries = Stream.of(Country.values())
            .filter(country -> country.getCssIconClass() != null && !country.getCssIconClass().startsWith("kwm"))
            .toList();

        countries.forEach(country -> assertThat(countryResolver.countryToAlpha2Code(country)).isNotBlank());
    }
}
