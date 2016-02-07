package de.fred4jupiter.fredbet.data;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.FredBetProfile;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.UserAlreadyExistsException;
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
		if (!environment.acceptsProfiles(FredBetProfile.INTEGRATION_TEST)) {
			createDefaultUsers();
		}

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
		matchService.save(MatchBuilder.create().withTeams(Country.FRANCE, Country.ROMANIA).withGroup(Group.GROUP_A)
				.withStadium("Saint-Denis").withKickOffDate(10, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ALBANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A).withStadium("Lens")
				.withKickOffDate(11, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ROMANIA, Country.SWITZERLAND).withGroup(Group.GROUP_A)
				.withStadium("Parc de Princes").withKickOffDate(15, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.FRANCE, Country.ALBANIA).withGroup(Group.GROUP_A).withStadium("Marseille")
				.withKickOffDate(15, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.SWITZERLAND, Country.FRANCE).withGroup(Group.GROUP_A).withStadium("Lille")
				.withKickOffDate(19, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ROMANIA, Country.ALBANIA).withGroup(Group.GROUP_A).withStadium("Lyon")
				.withKickOffDate(19, 6, 21).build());
	}

	private void createGroupB() {
		matchService.save(MatchBuilder.create().withTeams(Country.WALES, Country.SLOVAKIA).withGroup(Group.GROUP_B).withStadium("Bordeaux")
				.withKickOffDate(11, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ENGLAND, Country.RUSSIA).withGroup(Group.GROUP_B).withStadium("Marseille")
				.withKickOffDate(11, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.RUSSIA, Country.SLOVAKIA).withGroup(Group.GROUP_B).withStadium("Lille")
				.withKickOffDate(15, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ENGLAND, Country.WALES).withGroup(Group.GROUP_B).withStadium("Lens")
				.withKickOffDate(16, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.SLOVAKIA, Country.ENGLAND).withGroup(Group.GROUP_B)
				.withStadium("St. Etienne").withKickOffDate(20, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.RUSSIA, Country.WALES).withGroup(Group.GROUP_B).withStadium("Toulouse")
				.withKickOffDate(20, 6, 21).build());
	}

	private void createGroupC() {
		matchService.save(MatchBuilder.create().withTeams(Country.POLAND, Country.NORTH_IRLAND).withGroup(Group.GROUP_C)
				.withStadium("Nizza").withKickOffDate(12, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.GERMANY, Country.UKRAINE).withGroup(Group.GROUP_C).withStadium("Lille")
				.withKickOffDate(12, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.UKRAINE, Country.NORTH_IRLAND).withGroup(Group.GROUP_C)
				.withStadium("Lyon").withKickOffDate(16, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.GERMANY, Country.POLAND).withGroup(Group.GROUP_C).withStadium("St. Denis")
				.withKickOffDate(16, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.UKRAINE, Country.POLAND).withGroup(Group.GROUP_C).withStadium("Marseille")
				.withKickOffDate(21, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.NORTH_IRLAND, Country.GERMANY).withGroup(Group.GROUP_C)
				.withStadium("Parc de Princes").withKickOffDate(21, 6, 18).build());
	}

	private void createGroupD() {
		matchService.save(MatchBuilder.create().withTeams(Country.TURKEY, Country.CROATIA).withGroup(Group.GROUP_D)
				.withStadium("Parc de Princes").withKickOffDate(12, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.SPAIN, Country.CZECH_REPUBLIC).withGroup(Group.GROUP_D)
				.withStadium("Toulouse").withKickOffDate(13, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.CZECH_REPUBLIC, Country.CROATIA).withGroup(Group.GROUP_D)
				.withStadium("St. Etienne").withKickOffDate(17, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.SPAIN, Country.TURKEY).withGroup(Group.GROUP_D).withStadium("Nizza")
				.withKickOffDate(17, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.CROATIA, Country.SPAIN).withGroup(Group.GROUP_D).withStadium("Bordeaux")
				.withKickOffDate(21, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.CZECH_REPUBLIC, Country.TURKEY).withGroup(Group.GROUP_D)
				.withStadium("Lens").withKickOffDate(21, 6, 21).build());
	}

	private void createGroupE() {
		matchService.save(MatchBuilder.create().withTeams(Country.IRELAND, Country.SWEDEN).withGroup(Group.GROUP_E).withStadium("St. Denis")
				.withKickOffDate(13, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.BELGIUM, Country.ITALY).withGroup(Group.GROUP_E).withStadium("Lyon")
				.withKickOffDate(13, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ITALY, Country.SWEDEN).withGroup(Group.GROUP_E).withStadium("Toulouse")
				.withKickOffDate(17, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.BELGIUM, Country.IRELAND).withGroup(Group.GROUP_E).withStadium("Bordeaux")
				.withKickOffDate(18, 6, 15).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ITALY, Country.IRELAND).withGroup(Group.GROUP_E).withStadium("Lille")
				.withKickOffDate(22, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.SWEDEN, Country.BELGIUM).withGroup(Group.GROUP_E).withStadium("Nizza")
				.withKickOffDate(22, 6, 21).build());
	}

	private void createGroupF() {
		matchService.save(MatchBuilder.create().withTeams(Country.AUSTRIA, Country.HUNGARY).withGroup(Group.GROUP_F).withStadium("Bordeaux")
				.withKickOffDate(14, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.PORTUGAL, Country.ICELAND).withGroup(Group.GROUP_F)
				.withStadium("St. Etienne").withKickOffDate(14, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ICELAND, Country.HUNGARY).withGroup(Group.GROUP_F)
				.withStadium("Marseille").withKickOffDate(18, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.PORTUGAL, Country.AUSTRIA).withGroup(Group.GROUP_F)
				.withStadium("Parc de Princes").withKickOffDate(18, 6, 21).build());

		matchService.save(MatchBuilder.create().withTeams(Country.HUNGARY, Country.PORTUGAL).withGroup(Group.GROUP_F).withStadium("Lyon")
				.withKickOffDate(22, 6, 18).build());

		matchService.save(MatchBuilder.create().withTeams(Country.ICELAND, Country.AUSTRIA).withGroup(Group.GROUP_F)
				.withStadium("St. Denis").withKickOffDate(22, 6, 18).build());
	}

	private void createDefaultUsers() {
		LOG.info("createDefaultUsers: creating default users ...");

		// admin user will also be used for remote shell login
		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword("admin", "admin").withRole(FredBetRole.ROLE_ADMIN).deletable(false)
				.build());

		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword("michael", "michael").withRole(FredBetRole.ROLE_ADMIN).build());

		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword("janz", "janz").withRole(FredBetRole.ROLE_USER_ADVANCED).build());

		saveIfNotPresent(
				AppUserBuilder.create().withUsernameAndPassword("joernf", "joernf").withRole(FredBetRole.ROLE_USER_ADVANCED).build());

		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword("edit", "edit").withRole(FredBetRole.ROLE_USER_ADVANCED).build());

		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword("normal", "normal").withRole(FredBetRole.ROLE_USER).build());
	}

	private void saveIfNotPresent(AppUser appUser) {
		try {
			userService.insertAppUser(appUser);
		} catch (UserAlreadyExistsException e) {
			LOG.info(e.getMessage());
		}
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
