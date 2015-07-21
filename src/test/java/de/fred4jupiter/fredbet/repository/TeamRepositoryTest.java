package de.fred4jupiter.fredbet.repository;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;
import de.fred4jupiter.fredbet.domain.Team;

@ActiveProfiles("mongo-embedded")
public class TeamRepositoryTest extends AbstractIntegrationTest {

	@Rule
	public MongoDbRule mongoDbRule = MongoDbRuleBuilder.newMongoDbRule().defaultSpringMongoDb("demo-test");

	/**
	 *
	 * nosql-unit requirement
	 *
	 */
	@Autowired
	private ApplicationContext applicationContext;

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
