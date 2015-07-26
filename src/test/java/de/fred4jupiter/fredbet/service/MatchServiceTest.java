package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Match;

public class MatchServiceTest extends AbstractMongoEmbeddedTest{

	@Autowired
	private MatchService matchService;
	
	@Test
	public void createMatchAndFindAgain() {
		Match match = matchService.createAndSaveMatch("A", "B",	1, 1);
		assertNotNull(match);
		
		Match foundMatch = matchService.findMatchByMatchId(match.getId());
		assertNotNull(foundMatch);
		assertEquals(match, foundMatch);
	}
}
