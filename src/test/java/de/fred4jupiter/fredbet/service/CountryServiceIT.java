package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class CountryServiceIT {

    @Autowired
    private CountryService countryService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private DatabasePopulator databasePopulator;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        databasePopulator.deleteAllBetsAndMatches();
        userService.deleteAllUsers();
    }

    @Test
    public void getAvailableCountriesExtraBetsSortedWithNoneEntryByLocale() {
        createSomeMatches();
        matchRepository.flush();

        List<Country> countries = countryService.getAvailableCountriesExtraBetsSortedWithNoneEntryByLocale(Locale.GERMAN);
        assertNotNull(countries);
        assertThat(countries.size()).isEqualTo(5);
        assertThat(countries.get(0)).isEqualTo(Country.NONE);
        assertThat(countries.get(1)).isEqualTo(Country.BULGARIA);
        assertThat(countries.get(2)).isEqualTo(Country.GERMANY);
        assertThat(countries.get(3)).isEqualTo(Country.FRANCE);
        assertThat(countries.get(4)).isEqualTo(Country.IRELAND);
    }

    @Test
    public void getAvailableCountries() {
        List<Country> countries = countryService.getAvailableCountriesSortedWithNoneEntryByLocale(Locale.GERMAN, Group.GROUP_A);
        assertNotNull(countries);

        assertThat(countries).contains(Country.NONE);
    }

    private void createSomeMatches() {
        matchRepository.save(MatchBuilder.create().withTeams(Country.GERMANY, Country.FRANCE).withGroup(Group.GROUP_B)
            .withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(20)).withGoals(1, 2).build());

        matchRepository.save(MatchBuilder.create().withTeams(Country.BULGARIA, Country.IRELAND).withGroup(Group.GROUP_A)
            .withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(10)).withGoals(1, 2).build());
    }

}
