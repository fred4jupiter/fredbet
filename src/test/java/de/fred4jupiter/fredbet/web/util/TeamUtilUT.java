package de.fred4jupiter.fredbet.web.util;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class TeamUtilUT {

    @InjectMocks
    private TeamUtil teamUtil;

    @Mock
    private MessageSourceUtil messageSourceUtil;

    @Test
    void mapCodes() {
        assertThat(teamUtil.cssClassFor(Country.PHILIPPINES)).isEqualTo("fi fi-ph");
    }

    @Test
    void mapCodeForChampionsLeagueMember() {
        assertThat(teamUtil.cssClassFor(Country.MANCHESTER_CITY)).isEqualTo("kwm kwm-manchester");
    }
}
