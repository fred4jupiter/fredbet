package de.fred4jupiter.fredbet.data;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.Result;
import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.repository.AppUserRepository;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.repository.TeamRepository;

@Component
@Profile("demodata")
public class DataBasePopulator {

	private static final Logger LOG = LoggerFactory.getLogger(DataBasePopulator.class);

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private MatchRepository matchRepository;

	@PostConstruct
	public void initDatabaseWithDemoData() {
		LOG.info("initDatabaseWithDemoData: inserting demo data...");

		Team germany = new Team("Deutschland");
		teamRepository.save(germany);

		Team italy = new Team("Italien");
		teamRepository.save(italy);

		AppUser appUser = new AppUser("michael", "michael", "ROLE_USER");
		appUserRepository.save(appUser);

		Match match = new Match(germany, italy);
		match.setGroup("Gruppe A");
		match.setResult(new Result(3,2));
		matchRepository.save(match);

		Bet bet = new Bet(appUser.getUsername(), match, new Result(2,1));
		betRepository.save(bet);		
	}
}
