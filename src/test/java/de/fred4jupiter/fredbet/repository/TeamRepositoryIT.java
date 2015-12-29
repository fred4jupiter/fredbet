package de.fred4jupiter.fredbet.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.fred4jupiter.fredbet.AbstractMongoEmbeddedTest;
import de.fred4jupiter.fredbet.domain.Team;

public class TeamRepositoryIT extends AbstractMongoEmbeddedTest {

	@Autowired
	private TeamRepository teamRepository;

	@Test
	public void createAndSaveTeamAndLoadAgain() {
		Team team = new Team("Deutschland");

		team = teamRepository.save(team);
		assertNotNull(team);
		assertNotNull(team.getId());

		Team foundTeam = teamRepository.findOne(team.getId());
		assertEquals(team, foundTeam);
	}
}
