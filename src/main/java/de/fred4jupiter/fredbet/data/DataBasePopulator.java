package de.fred4jupiter.fredbet.data;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.service.MatchService;

@Component
@Profile("demodata")
public class DataBasePopulator {

	private static final Logger LOG = LoggerFactory.getLogger(DataBasePopulator.class);

	@Autowired
	private MatchService matchService;

	@PostConstruct
	public void initDatabaseWithDemoData() {
		LOG.info("initDatabaseWithDemoData: inserting demo data...");

		// for (int i = 0; i < 12; i++) {
		// matchService.createAndSaveMatch("Deutschland", "Italien", 2, 1);
		// }

		matchService.save(MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
				.withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(5)).build());
		
		matchService.save(MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
				.withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(10)).build());
		
		matchService.save(MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D)
				.withStadium("AOL Arena, München").withKickOffDate(LocalDateTime.now().plusMinutes(15)).build());

	}
}
