package de.fred4jupiter.fredbet.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.info.InfoType;

@Component
public class DataBasePopulator {

	private static final int NUMBER_OF_DEMO_USERS = 12;

	private static final String DEFAULT_PASSWORD_ADMIN_USER = FredbetConstants.TECHNICAL_USERNAME;

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

	@Autowired
	private InfoService infoService;

	@Autowired
	private ImageAdministrationService imageAdministrationService;

	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;

	@PostConstruct
	private void initDatabaseWithDemoData() {
		if (!isRunInIntegrationTest()) {
			createDefaultUsers();
			addRulesIfEmpty();
		}

		if (!isRunInIntegrationTest() && runtimeConfigurationService.loadRuntimeConfig().isCreateDemoData()) {
			createDemoUsers();
			createRandomMatches();
		}

		imageAdministrationService.createDefaultImageGroup();
	}

	private boolean isRunInIntegrationTest() {
		return environment.acceptsProfiles(FredBetProfile.INTEGRATION_TEST);
	}

	public void createRandomMatches() {
		bettingService.deleteAllBets();
		matchService.deleteAllMatches();

		LocalDateTime localDateTime = LocalDateTime.now().plusHours(1);

		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_A, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_B, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_C, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_D, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_E, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_F, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_G, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.GROUP_H, 4);

		localDateTime = createRandomForGroup(localDateTime, Group.ROUND_OF_SIXTEEN, 8);
		localDateTime = createRandomForGroup(localDateTime, Group.QUARTER_FINAL, 4);
		localDateTime = createRandomForGroup(localDateTime, Group.SEMI_FINAL, 2);
		localDateTime = createRandomForGroup(localDateTime, Group.FINAL, 1);
		localDateTime = createRandomForGroup(localDateTime, Group.GAME_FOR_THIRD, 1);
	}

	private LocalDateTime createRandomForGroup(LocalDateTime localDateTime, Group group, int numberOfMatches) {
		LocalDateTime tmpTime = localDateTime;
		for (int i = 0; i < numberOfMatches; i++) {
			ImmutablePair<Country, Country> teamPair = randomValueGenerator.generateTeamPair();
			Match match = MatchBuilder.create().withTeams(teamPair.getLeft(), teamPair.getRight()).withGroup(group).withStadium("Somewhere")
					.withKickOffDate(tmpTime).build();
			matchService.save(match);

			tmpTime = tmpTime.plusDays(1).plusMinutes(10);
		}

		return tmpTime;
	}

	public void createDemoBetsForAllUsers() {
		LOG.info("createDemoBetsForAllUsers...");
		bettingService.deleteAllBets();

		List<Match> allMatches = matchService.findAll();
		List<AppUser> users = userService.findAll();
		users.forEach(appUser -> {
			for (Match match : allMatches) {
				createBetForUser(appUser, match, false);
			}

			createExtraBetForUser(appUser);
		});
	}

	private void createExtraBetForUser(AppUser appUser) {
		ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
		if (triple != null) {
			Country extraBetCountryFinalWinner = triple.getLeft();
			Country extraBetCountrySemiFinalWinner = triple.getMiddle();
			Country extraBetCountryThirdFinalWinner = triple.getRight();
			bettingService.saveExtraBet(extraBetCountryFinalWinner, extraBetCountrySemiFinalWinner, extraBetCountryThirdFinalWinner,
					appUser.getUsername());
		}
	}

	private void createBetForUser(AppUser appUser, Match match, boolean joker) {
		Integer goalsTeamOne = randomValueGenerator.generateRandomValue();
		Integer goalsTeamTwo = randomValueGenerator.generateRandomValue();
		bettingService.createAndSaveBetting(appUser, match, goalsTeamOne, goalsTeamTwo, joker);
	}

	public void createDemoResultsForAllMatches() {
		LOG.info("createDemoResultsForAllUsers...");

		List<Match> allMatches = matchService.findAll();
		allMatches.forEach(match -> {
			match.enterResult(randomValueGenerator.generateRandomValue(), randomValueGenerator.generateRandomValue());
			matchService.save(match);
		});
	}

	private void addRulesIfEmpty() {
		ClassPathResource classPathResource = new ClassPathResource("content/rules_de.txt");
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			IOUtils.copyLarge(classPathResource.getInputStream(), byteOut);
			String rulesInGerman = byteOut.toString("UTF-8");

			Locale locale = LocaleContextHolder.getLocale();
			infoService.saveInfoContentIfNotPresent(InfoType.RULES, rulesInGerman, locale);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void createDemoUsers() {
		LOG.info("createAdditionalUsers: creating additional demo users ...");

		final byte[] demoImage = loadDemoUserProfileImage();

		final int numberOfDemoUsers = NUMBER_OF_DEMO_USERS;
		for (int i = 1; i <= numberOfDemoUsers; i++) {
			String usernameAndPassword = "test" + i;
			AppUser user = AppUserBuilder.create().withUsernameAndPassword(usernameAndPassword, usernameAndPassword)
					.withRole(FredBetRole.ROLE_USER).build();
			boolean isNewUser = saveIfNotPresent(user);
			if (isNewUser && (numberOfDemoUsers % i == 0)) {
				this.imageAdministrationService.saveUserProfileImage(demoImage, user, null);
			}
		}
	}

	private byte[] loadDemoUserProfileImage() {
		ClassPathResource classPathResource = new ClassPathResource("static/images/profile_demo_image.jpg");
		try (InputStream in = classPathResource.getInputStream()) {
			return IOUtils.toByteArray(in);
		} catch (IOException e) {
			throw new IllegalStateException("Could not load demo image from classpath. " + e.getMessage());
		}
	}

	private void createDefaultUsers() {
		LOG.info("createDefaultUsers: creating default users ...");

		// admin user will also be used for remote shell login
		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword(FredbetConstants.TECHNICAL_USERNAME, DEFAULT_PASSWORD_ADMIN_USER)
				.withRole(FredBetRole.ROLE_ADMIN).deletable(false).build());

		saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword("michael", "michael").withRole(FredBetRole.ROLE_ADMIN).build());
	}

	public boolean saveIfNotPresent(AppUser appUser) {
		try {
			userService.createUser(appUser);
			return true;
		} catch (UserAlreadyExistsException e) {
			LOG.debug(e.getMessage());
			return false;
		}
	}

	@Transactional
	public void deleteAllBetsAndMatches() {
		bettingService.deleteAllBets();
		matchService.deleteAllMatches();
	}

}
