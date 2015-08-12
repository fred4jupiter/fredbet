package de.fred4jupiter.fredbet.data;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.DateUtils;

@Component
@Profile("demodata")
public class DataBasePopulator {

	private static final Logger LOG = LoggerFactory.getLogger(DataBasePopulator.class);

	@Autowired
	private MatchService matchService;

	
	@PostConstruct
	public void initDatabaseWithDemoData() {
		LOG.info("initDatabaseWithDemoData: inserting demo data...");

//		for (int i = 0; i < 12; i++) {
//			matchService.createAndSaveMatch("Deutschland", "Italien", 2, 1);
//		}

		Match match = MatchBuilder.create().withTeams("Bulgarien", "Irland").withGoals(3, 5).withGroup("Gruppe A").build();
		match.setStadium("Westfalenstadium, Dortmund");
		match.setKickOffDate(DateUtils.toDate(LocalDateTime.now().plusMinutes(5)));
		matchService.save(match);

	}
}
