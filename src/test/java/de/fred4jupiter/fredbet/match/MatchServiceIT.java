package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class MatchServiceIT {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private DataPopulator dataPopulator;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    public void setup() {
        dataPopulator.deleteAllBetsAndMatches();
        userService.deleteAllUsers();
        matchService.deleteAllMatches();
    }

    @Test
    public void createMatchAndFindAgain() {
        Match match = MatchBuilder.create().withGroup(Group.GROUP_A).withTeams("A", "B").withGoals(1, 1).build();
        assertNotNull(match);
        matchService.save(match);

        Match foundMatch = matchService.findByMatchId(match.getId());
        assertNotNull(foundMatch);
        assertEquals(match, foundMatch);
    }

    @Test
    public void createMatchAndFindAgainByCountry() {
        Match match = MatchBuilder.create().withGroup(Group.GROUP_A).withTeams(Country.ALBANIA, Country.SWITZERLAND).withGoals(1, 1).build();
        assertNotNull(match);
        matchService.save(match);

        Match foundMatch = matchService.findByMatchId(match.getId());
        assertNotNull(foundMatch);
        assertEquals(match, foundMatch);
    }

    @Test
    public void createMatchTwiceAndCheckIfOnlyOneIsPresent() {
        final Country countryOne = Country.ALBANIA;
        Match match = MatchBuilder.create()
            .withGroup(Group.GROUP_A)
            .withTeams(countryOne, Country.SWITZERLAND)
            .withKickOffDate(LocalDateTime.now().plusHours(1))
            .withGoals(1, 1)
            .build();
        assertNotNull(match);
        Match firstSaved = matchService.save(match);
        assertThat(firstSaved).isNotNull();
        Match firstSavedFound = matchService.findMatchById(firstSaved.getId());
        assertThat(firstSavedFound).isNotNull();

        matchService.save(match);

        List<Match> foundList = matchRepository.findByTeamOneCountry(countryOne);
        assertEquals(1, foundList.size());
    }

    @Test
    public void createTwoMatches() {
        Match match1 = MatchBuilder.create().withTeams(Country.ALBANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A).withStadium("Lens")
            .withKickOffDate(11, 6, 15).build();
        assertThat(match1.getTeamOne().getCountry()).isEqualTo(Country.ALBANIA);
        assertThat(match1.getTeamTwo().getCountry()).isEqualTo(Country.SWITZERLAND);

        matchService.save(match1);

        Match match2 = MatchBuilder.create().withTeams(Country.ROMANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A)
            .withStadium("Parc de Princes").withKickOffDate(15, 6, 18).build();
        assertThat(match2.getTeamOne().getCountry()).isEqualTo(Country.ROMANIA);
        assertThat(match2.getTeamTwo().getCountry()).isEqualTo(Country.SWITZERLAND);
        matchService.save(match2);

        Match foundMatchOne = matchRepository.getReferenceById(match1.getId());
        assertThat(foundMatchOne).isNotNull();
        assertThat(foundMatchOne.getTeamOne()).isEqualTo(match1.getTeamOne());
        assertThat(foundMatchOne.getTeamTwo()).isEqualTo(match1.getTeamTwo());

        Match foundMatchTwo = matchRepository.getReferenceById(match2.getId());
        assertThat(foundMatchTwo).isNotNull();
        assertThat(foundMatchTwo.getTeamOne()).isEqualTo(match2.getTeamOne());
        assertThat(foundMatchTwo.getTeamTwo()).isEqualTo(match2.getTeamTwo());

        assertThat(matchRepository.count()).isEqualTo(2);

        assertThat(teamRepository.count()).isEqualTo(3);
    }

    @Test
    public void changeTeamInMatch() {
        Match match = MatchBuilder.create().withTeams(Country.ALBANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A).withStadium("Lens")
            .withKickOffDate(11, 6, 15).build();
        matchService.save(match);

        Match found = matchRepository.getReferenceById(match.getId());
        assertEquals(match, found);

        final Country newCountry = Country.ENGLAND;
        found.getTeamOne().setCountry(newCountry);

        matchService.save(found);

        Match found2 = matchRepository.getReferenceById(found.getId());
        assertEquals(newCountry, found2.getTeamOne().getCountry());
    }
}
