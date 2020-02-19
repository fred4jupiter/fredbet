package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CountryServiceIT extends AbstractIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(CountryServiceIT.class);

    @Autowired
    private CountryService countryService;

    @Test
    public void availableCountriesDoesNotContainNoneEntry() {
        List<Country> countries = countryService.getAvailableCountriesSortedWithoutNoneEntry(Locale.GERMAN);
        assertNotNull(countries);

        assertThat(countries).doesNotContain(Country.NONE);

        LOG.debug("" + countries);
    }

    @Test
    public void getAvailableCountries() {
        List<Country> countries = countryService.getAvailableCountriesSortedWithNoneEntryByLocale(Locale.GERMAN, Group.GROUP_A);
        assertNotNull(countries);

        assertThat(countries).contains(Country.NONE);
    }

}
