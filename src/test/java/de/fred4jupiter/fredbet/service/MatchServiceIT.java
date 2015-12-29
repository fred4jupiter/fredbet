package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;

public class MatchServiceIT extends AbstractMongoEmbeddedTest {

	@Autowired
	private MatchService matchService;

	@Test
	public void createMatchAndFindAgain() {
		Match match = MatchBuilder.create().withTeams("A", "B").withGoals(1, 1).build();
		assertNotNull(match);
		matchService.save(match);

		Match foundMatch = matchService.findMatchByMatchId(match.getId());
		assertNotNull(foundMatch);
		assertEquals(match, foundMatch);
	}
}
