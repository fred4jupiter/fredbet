package de.fred4jupiter.fredbet.data;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.FredBetProfile;
import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.service.BettingService;
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
	private BettingService bettingService;

	@Autowired
	private RandomValueGenerator randomValueGenerator;

	@PostConstruct
	private void initDatabaseWithDemoData() {
		createDefaultUsers();

		if (environment.acceptsProfiles(FredBetProfile.DEMODATA)) {
			createEM2016Matches();
		}
	}

	/**
	 * Deletes all current bets and matches and inserts new demo data.
	 */
	public void createEM2016Matches() {
		LOG.info("createDemoData: deleting all existend bets and matches ...");
		bettingService.deleteAllBets();
		matchService.deleteAllMatches();

		createMatches();
	}

	private void createMatches() {
		LOG.info("createMatches: inserting demo matches ...");
		createGroupA();
		createGroupB();
		createGroupC();
		createGroupD();
		createGroupE();
		createGroupF();
		createRoundOfSixteen();
		createQuarterFinal();
		createSemiFinal();
		createFinal();
	}

	private void createFinal() {
		matchService.save(MatchBuilder.create().withTeams("Sieger HF1", "Sieger HF2").withGroup(Group.FINAL).withStadium("St. Denis")
				.withKickOffDate(10, 7, 21).build());
	}

	private void createSemiFinal() {
		matchService.save(MatchBuilder.create().withTeams("Sieger VF1", "Sieger VF2").withGroup(Group.SEMI_FINAL).withStadium("Lyon")
				.withKickOffDate(6, 7, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger VF3", "Sieger VF4").withGroup(Group.SEMI_FINAL).withStadium("Marseille")
				.withKickOffDate(7, 7, 21).build());
	}

	private void createQuarterFinal() {
		matchService.save(MatchBuilder.create().withTeams("Sieger AF1", "Sieger AF3").withGroup(Group.QUARTER_FINAL)
				.withStadium("Marseille").withKickOffDate(30, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger AF2", "Sieger AF6").withGroup(Group.QUARTER_FINAL).withStadium("Lille")
				.withKickOffDate(1, 7, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger AF5", "Sieger AF7").withGroup(Group.QUARTER_FINAL).withStadium("Bordeaux")
				.withKickOffDate(2, 7, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger AF4", "Sieger AF8").withGroup(Group.QUARTER_FINAL)
				.withStadium("St. Denis").withKickOffDate(3, 7, 21).build());
	}

	private void createRoundOfSixteen() {
		matchService.save(MatchBuilder.create().withTeams("Zweiter A", "Zweiter B").withGroup(Group.ROUND_OF_SIXTEEN)
				.withStadium("St. Etienne").withKickOffDate(25, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger B", "Dritter A/C/D").withGroup(Group.ROUND_OF_SIXTEEN)
				.withStadium("Parc de Princes").withKickOffDate(25, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger D", "Dritter B/E/F").withGroup(Group.ROUND_OF_SIXTEEN).withStadium("Lens")
				.withKickOffDate(25, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger A", "Dritter C/D/E").withGroup(Group.ROUND_OF_SIXTEEN).withStadium("Lyon")
				.withKickOffDate(26, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger C", "Dritter A/B/F").withGroup(Group.ROUND_OF_SIXTEEN)
				.withStadium("Lille").withKickOffDate(26, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger F", "Zweiter E").withGroup(Group.ROUND_OF_SIXTEEN).withStadium("Toulouse")
				.withKickOffDate(26, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger E", "Zweiter D").withGroup(Group.ROUND_OF_SIXTEEN)
				.withStadium("St. Denis").withKickOffDate(27, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Sieger B", "Zweiter F").withGroup(Group.ROUND_OF_SIXTEEN).withStadium("Nizza")
				.withKickOffDate(27, 6, 21).build());
	}

	private void createGroupA() {
		matchService.save(MatchBuilder.create().withTeams("Frankreich", "Rumänien").withGroup(Group.GROUP_A).withStadium("Saint-Denis")
				.withKickOffDate(10, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Albanien", "Schweiz").withGroup(Group.GROUP_A).withStadium("Lens")
				.withKickOffDate(11, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Rumänien", "Schweiz").withGroup(Group.GROUP_A).withStadium("Parc de Princes")
				.withKickOffDate(15, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Frankreich", "Albanien").withGroup(Group.GROUP_A).withStadium("Marseille")
				.withKickOffDate(15, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Schweiz", "Frankreich").withGroup(Group.GROUP_A).withStadium("Lille")
				.withKickOffDate(19, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Rumänien", "Albanien").withGroup(Group.GROUP_A).withStadium("Lyon")
				.withKickOffDate(19, 6, 21).build());
	}

	private void createGroupB() {
		matchService.save(MatchBuilder.create().withTeams("Wales", "Slowakei").withGroup(Group.GROUP_B).withStadium("Bordeaux")
				.withKickOffDate(11, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("England", "Russland").withGroup(Group.GROUP_B).withStadium("Marseille")
				.withKickOffDate(11, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Russland", "Slowakei").withGroup(Group.GROUP_B).withStadium("Lille")
				.withKickOffDate(15, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("England", "Wales").withGroup(Group.GROUP_B).withStadium("Lens")
				.withKickOffDate(16, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Slowakei", "England").withGroup(Group.GROUP_B).withStadium("St. Etienne")
				.withKickOffDate(20, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Russland", "Wales").withGroup(Group.GROUP_B).withStadium("Toulouse")
				.withKickOffDate(20, 6, 21).build());
	}

	private void createGroupC() {
		matchService.save(MatchBuilder.create().withTeams("Polen", "Nordirland").withGroup(Group.GROUP_C).withStadium("Nizza")
				.withKickOffDate(12, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Deutschland", "Ukraine").withGroup(Group.GROUP_C).withStadium("Lille")
				.withKickOffDate(12, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Ukraine", "Nordirland").withGroup(Group.GROUP_C).withStadium("Lyon")
				.withKickOffDate(16, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Deutschland", "Polen").withGroup(Group.GROUP_C).withStadium("St. Denis")
				.withKickOffDate(16, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Ukraine", "Polen").withGroup(Group.GROUP_C).withStadium("Marseille")
				.withKickOffDate(21, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Nordirland", "Deutschland").withGroup(Group.GROUP_C)
				.withStadium("Parc de Princes").withKickOffDate(21, 6, 18).build());
	}

	private void createGroupD() {
		matchService.save(MatchBuilder.create().withTeams("Türkei", "Kroatien").withGroup(Group.GROUP_D).withStadium("Parc de Princes")
				.withKickOffDate(12, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Spanien", "Tschechien").withGroup(Group.GROUP_D).withStadium("Toulouse")
				.withKickOffDate(13, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Tschechien", "Kroatien").withGroup(Group.GROUP_D).withStadium("St. Etienne")
				.withKickOffDate(17, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Spanien", "Türkei").withGroup(Group.GROUP_D).withStadium("Nizza")
				.withKickOffDate(17, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Kroatien", "Spanien").withGroup(Group.GROUP_D).withStadium("Bordeaux")
				.withKickOffDate(21, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Tschechien", "Türkei").withGroup(Group.GROUP_D).withStadium("Lens")
				.withKickOffDate(21, 6, 21).build());
	}

	private void createGroupE() {
		matchService.save(MatchBuilder.create().withTeams("Irland", "Schweden").withGroup(Group.GROUP_E).withStadium("St. Denis")
				.withKickOffDate(13, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Belgien", "Italien").withGroup(Group.GROUP_E).withStadium("Lyon")
				.withKickOffDate(13, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Italien", "Schweden").withGroup(Group.GROUP_E).withStadium("Toulouse")
				.withKickOffDate(17, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Belgien", "Irland").withGroup(Group.GROUP_E).withStadium("Bordeaux")
				.withKickOffDate(18, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams("Italien", "Irland").withGroup(Group.GROUP_E).withStadium("Lille")
				.withKickOffDate(22, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Schweden", "Belgien").withGroup(Group.GROUP_E).withStadium("Nizza")
				.withKickOffDate(22, 6, 21).build());
	}

	private void createGroupF() {
		matchService.save(MatchBuilder.create().withTeams("Österreich", "Ungarn").withGroup(Group.GROUP_F).withStadium("Bordeaux")
				.withKickOffDate(14, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Portugal", "Island").withGroup(Group.GROUP_F).withStadium("St. Etienne")
				.withKickOffDate(14, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Island", "Ungarn").withGroup(Group.GROUP_F).withStadium("Marseille")
				.withKickOffDate(18, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Portugal", "Österreich").withGroup(Group.GROUP_F).withStadium("Parc de Princes")
				.withKickOffDate(18, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams("Ungarn", "Portugal").withGroup(Group.GROUP_F).withStadium("Lyon")
				.withKickOffDate(22, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams("Island", "Österreich").withGroup(Group.GROUP_F).withStadium("St. Denis")
				.withKickOffDate(22, 6, 18).build());
	}

	private void createDefaultUsers() {
		LOG.info("createDefaultUsers: creating default users ...");
		// will also be used for remote shell login
		userService.save(
				new AppUser("admin", "admin", false, FredBetRole.ROLE_USER, FredBetRole.ROLE_ADMIN, FredBetRole.ROLE_EDIT_MATCH));
		userService.save(new AppUser("michael", "michael", FredBetRole.ROLE_USER, FredBetRole.ROLE_ADMIN, FredBetRole.ROLE_EDIT_MATCH));

		userService.save(new AppUser("janz", "janz", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));
		userService.save(new AppUser("joernf", "joernf", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));

		userService.save(new AppUser("edit", "edit", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));
		userService.save(new AppUser("normal", "normal", FredBetRole.ROLE_USER));
	}

	public void createDemoBetsForAllUsers() {
		List<Match> allMatches = matchService.findAll();
		List<AppUser> users = userService.findAll();
		users.forEach(appUser -> {
			allMatches.forEach(match -> {
				Integer goalsTeamOne = randomValueGenerator.generateRandomValue();
				Integer goalsTeamTwo = randomValueGenerator.generateRandomValue();
				bettingService.createAndSaveBetting(appUser, match, goalsTeamOne, goalsTeamTwo);
			});
		});

	}

	public void createDemoResultsForAllUsers() {
		List<Match> allMatches = matchService.findAll();
		allMatches.forEach(match -> {
			match.enterResult(randomValueGenerator.generateRandomValue(), randomValueGenerator.generateRandomValue());
			matchService.save(match);
		});
	}
}
