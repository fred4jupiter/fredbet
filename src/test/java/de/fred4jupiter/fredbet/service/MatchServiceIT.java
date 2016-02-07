package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.repository.MatchRepository;

public class MatchServiceIT extends AbstractMongoEmbeddedTest {

	@Autowired
	private MatchService matchService;

	@Autowired
	private MatchRepository matchRepository;

	@Before
	public void setup() {
		matchRepository.deleteAll();
	}

	@Test
	public void createMatchAndFindAgain() {
		Match match = MatchBuilder.create().withTeams("A", "B").withGoals(1, 1).build();
		assertNotNull(match);
		matchService.save(match);

		Match foundMatch = matchService.findMatchByMatchId(match.getId());
		assertNotNull(foundMatch);
		assertEquals(match, foundMatch);
	}

	@Test
	public void createMatchAndFindAgainByCountry() {
		Match match = MatchBuilder.create().withTeams(Country.ALBANIA, Country.SWITZERLAND).withGoals(1, 1).build();
		assertNotNull(match);
		matchService.save(match);

		Match foundMatch = matchService.findMatchByMatchId(match.getId());
		assertNotNull(foundMatch);
		assertEquals(match, foundMatch);
	}

	@Test
	public void createMatchTwiceAndCheckIfOnlyOneIsPresent() {
		final Country countryOne = Country.ALBANIA;
		Match match = MatchBuilder.create().withTeams(countryOne, Country.SWITZERLAND).withGoals(1, 1).build();
		assertNotNull(match);
		matchService.save(match);
		matchService.save(match);

		List<Match> foundList = matchRepository.findByCountryOne(countryOne);
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

		Match found = matchRepository.findOne(match.getId());
		assertEquals(match, found);

		final Country newCountry = Country.ENGLAND;
		found.setCountryOne(newCountry);

		matchService.save(found);

		Match found2 = matchRepository.findOne(found.getId());
		assertEquals(newCountry, found2.getCountryOne());
	}
}
