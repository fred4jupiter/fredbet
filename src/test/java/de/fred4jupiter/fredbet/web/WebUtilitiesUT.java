package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.web.util.LanguageIconUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class WebUtilitiesUT {

    @Mock
    private MatchService matchService;

    @Test
    public void buildUrlReplacesLanguageQueryParameter() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/ranking");
        request.setQueryString("foo=bar&lang=de");
        request.addParameter("foo", "bar");
        request.addParameter("lang", "de");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String url = new LanguageUtil().buildUrl("en");

        assertThat(url).isEqualTo("http://localhost/ranking?foo=bar&lang=en");
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void cssClassCurrentUserLanguageHandlesSpecialMappings() {
        LanguageIconUtil util = new LanguageIconUtil();
        Locale previous = LocaleContextHolder.getLocale();
        try {
            LocaleContextHolder.setLocale(Locale.ENGLISH);
            assertThat(util.cssClassCurrentUserLanguage()).isEqualTo("fi-gb");

            LocaleContextHolder.setLocale(Locale.forLanguageTag("cs"));
            assertThat(util.cssClassCurrentUserLanguage()).isEqualTo("fi-cz");

            LocaleContextHolder.setLocale(Locale.forLanguageTag("ca"));
            assertThat(util.cssClassCurrentUserLanguage()).isEqualTo("fi-es-ct");

            LocaleContextHolder.setLocale(Locale.forLanguageTag("sv"));
            assertThat(util.cssClassCurrentUserLanguage()).isEqualTo("fi-se");
        } finally {
            LocaleContextHolder.setLocale(previous);
        }
    }

    @Test
    public void groupAvailabilityUtilSortsAvailableGroupsAndCalculatesDividers() {
        when(matchService.availableGroups()).thenReturn(Set.of(Group.FINAL, Group.GROUP_A, Group.SEMI_FINAL));
        GroupAvailabilityUtil util = new GroupAvailabilityUtil(matchService);

        List<Group> groups = util.getGroups();

        assertThat(groups).containsExactly(Group.GROUP_A, Group.SEMI_FINAL, Group.FINAL);
        assertThat(util.isGroupAvailable(Group.GROUP_A.name())).isTrue();
        assertThat(util.isDividerNecessary(Group.GROUP_A)).isTrue();
        assertThat(util.isDividerNecessary(Group.FINAL)).isTrue();
        assertThat(util.isDividerNecessary(Group.SEMI_FINAL)).isFalse();
        assertThat(util.isDividerNecessary(null)).isFalse();
    }

    @Test
    public void groupAvailabilityUtilRequiresRoundOfSixteenDividerWhenNoRoundOfThirtyTwoExists() {
        when(matchService.availableGroups()).thenReturn(Set.of(Group.ROUND_OF_SIXTEEN));
        GroupAvailabilityUtil util = new GroupAvailabilityUtil(matchService);

        assertThat(util.isDividerNecessary(Group.ROUND_OF_SIXTEEN)).isTrue();
    }

    @Test
    public void groupAvailabilityUtilRequiresRoundOfThirtyTwoDividerWhenAvailable() {
        when(matchService.availableGroups()).thenReturn(Set.of(Group.ROUND_OF_THIRTY_TWO));
        GroupAvailabilityUtil util = new GroupAvailabilityUtil(matchService);

        assertThat(util.isDividerNecessary(Group.ROUND_OF_THIRTY_TWO)).isTrue();
    }
}

