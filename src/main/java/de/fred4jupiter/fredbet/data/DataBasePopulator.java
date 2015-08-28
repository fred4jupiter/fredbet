package de.fred4jupiter.fredbet.data;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.UserService;

@Component
public class DataBasePopulator {

	private static final Logger LOG = LoggerFactory.getLogger(DataBasePopulator.class);

	@Autowired
	private MatchService matchService;

	@Autowired
	private Environment environment;

	@Autowired
	private UserService userService;

	@Autowired
	private BetRepository betRepository;

	@PostConstruct
	public void initDatabaseWithDemoData() {
		LOG.info("initDatabaseWithDemoData: inserting demo data...");

		final AppUser adminUser = new AppUser("admin", "admin", "ROLE_USER", "ROLE_ADMIN");
		userService.save(adminUser);
		final AppUser testUser = new AppUser("test", "test", "ROLE_USER");
		userService.save(testUser);

		// this we be executed in demodata profile only
		if (environment.acceptsProfiles("demodata")) {
			Match match1 = MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
					.withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(5)).build();
			matchService.save(match1);

			Match match2 = MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
					.withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(10)).build();
			matchService.save(match2);

			Match match3 = MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D).withStadium("AOL Arena, München")
					.withKickOffDate(LocalDateTime.now().plusMinutes(15)).build();
			matchService.save(match3);

			createAndSaveBetForWith(adminUser.getUsername(), 2, 1, match1);
			createAndSaveBetForWith(adminUser.getUsername(), 3, 1, match2);
			createAndSaveBetForWith(adminUser.getUsername(), 2, 4, match3);
			
			createAndSaveBetForWith(testUser.getUsername(), 0, 1, match1);
			createAndSaveBetForWith(testUser.getUsername(), 1, 2, match2);
			createAndSaveBetForWith(testUser.getUsername(), 5, 0, match3);
		}
	}

	private void createAndSaveBetForWith(String userName, int goalsTeamOne, int goalsTeamTwo, Match match) {
		Bet bet = new Bet();
		bet.setGoalsTeamOne(goalsTeamOne);
		bet.setGoalsTeamTwo(goalsTeamTwo);
		bet.setUserName(userName);
		bet.setMatch(match);
		betRepository.save(bet);
	}
}
