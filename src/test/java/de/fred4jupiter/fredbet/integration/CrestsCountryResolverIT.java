package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class CrestsCountryResolverIT {

    @Autowired
    private CrestsCountryResolver crestsCountryResolver;

    @Test
    void resolveCestsForAllCountries() {
        List<Country> countries = List.of(Country.values());

        countries.forEach(country -> {
            SvgImage crestsImage = crestsCountryResolver.loadCrestsImageFor(country);
            assertThat(crestsImage).isNotNull();
            assertThat(crestsImage.svgContent()).isNotBlank();
        });
    }
}
