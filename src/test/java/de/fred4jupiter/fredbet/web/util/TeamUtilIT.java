package de.fred4jupiter.fredbet.web.util;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class TeamUtilIT {

    @Autowired
    private TeamUtil teamUtil;

    @BeforeEach
    public void setup() {
        LocaleContextHolder.setDefaultLocale(Locale.ENGLISH);
    }

    @Test
    void resolveTeamName() {
        assertThat(teamUtil.i18n(Country.GERMANY)).isEqualTo("Germany");
    }

    @Test
    void resolveTeamNameForChampionsLeagueMember() {
        assertThat(teamUtil.i18n(Country.MANCHESTER_CITY)).isEqualTo("Manchester City");
    }
}
