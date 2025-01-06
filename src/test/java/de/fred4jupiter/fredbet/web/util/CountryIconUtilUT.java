package de.fred4jupiter.fredbet.web.util;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class CountryIconUtilUT {

    @Test
    void mapCodes() {
        CountryIconUtil countryIconUtil = new CountryIconUtil();
        assertThat(countryIconUtil.cssClassFor(Country.PHILIPPINES)).isEqualTo("fi-ph");
    }
}
