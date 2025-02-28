package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class MatchServiceIT {

    @Autowired
    private MatchService matchService;

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
        Match match = MatchBuilder.create().withGroup(Group.GROUP_A).withTeams(countryOne, Country.SWITZERLAND).withGoals(1, 1).build();
        assertNotNull(match);
        matchService.save(match);
        matchService.save(match);

        List<Match> foundList = matchRepository.findByTeamOneCountry(countryOne);
        assertEquals(1, foundList.size());
    }

    @Test
    public void createTwoMatches() {
        matchService.save(MatchBuilder.create().withTeams(Country.ALBANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A).withStadium("Lens")
            .withKickOffDate(11, 6, 15).build());

        matchService.save(MatchBuilder.create().withTeams(Country.ROMANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A)
            .withStadium("Parc de Princes").withKickOffDate(15, 6, 18).build());

        assertEquals(2, matchRepository.count());
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
