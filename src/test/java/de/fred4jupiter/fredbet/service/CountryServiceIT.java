package de.fred4jupiter.fredbet.service;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;

public class CountryServiceIT extends AbstractIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(CountryServiceIT.class);

    @Autowired
    private CountryService countryService;

    @Test
    public void availableCountriesDoesNotContainNoneEntry() {
        List<Country> countries = countryService.getAvailableCountriesSortedWithoutNoneEntry(Locale.GERMAN);
        assertNotNull(countries);

        assertThat(countries, not(hasItem(Country.NONE)));

        LOG.debug("" + countries);
    }

    @Test
    public void getAvailableCountries() {
        List<Country> countries = countryService.getAvailableCountriesSortedWithNoneEntryByLocale(Locale.GERMAN, Group.GROUP_A);
        assertNotNull(countries);

        assertThat(countries, hasItem(Country.NONE));
    }

}
