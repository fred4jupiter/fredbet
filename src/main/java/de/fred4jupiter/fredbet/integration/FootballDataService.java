package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class FootballDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataService.class);

    private final FredbetProperties fredbetProperties;

    private final MatchService matchService;

    private final BettingService bettingService;

    public FootballDataService(FredbetProperties fredbetProperties, MatchService matchService, BettingService bettingService) {
        this.fredbetProperties = fredbetProperties;
        this.matchService = matchService;
        this.bettingService = bettingService;
    }

    public void importData() {
        final FootballDataProperties footballDataProperties = this.fredbetProperties.integration().footballData();
        RestClient restClient = RestClient.builder().baseUrl(footballDataProperties.baseUrl()).defaultHeader("X-Auth-Token", footballDataProperties.apiToken()).build();
        FdMatches fdMatches = restClient.get().uri("/competitions/WC/matches?season=2026").attribute("season", 2026).retrieve().body(FdMatches.class);
        LOG.debug("Response from Football Data: fdMatches={}", fdMatches);
        importMatches(fdMatches);
    }

    private void importMatches(FdMatches fdMatches) {
        if (fdMatches == null) {
            LOG.warn("Could not load football data matches!");
            return;
        }
        LOG.debug("Importing {} Football Data matches", fdMatches.matches().size());

        bettingService.deleteAllBets();
        LOG.info("deleted all bets");

        matchService.deleteAllMatches();
        LOG.info("deleted all matches");

        List<FdMatch> matches = fdMatches.matches();

        matches.forEach(fdMatch -> {
            Match match = mapToMatch(fdMatch);
            if (match != null) {
                matchService.save(match);
            }
        });
        LOG.debug("imported {} matches", matches.size());
    }

    private Match mapToMatch(FdMatch fdMatch) {
        if (fdMatch == null || fdMatch.homeTeam() == null || fdMatch.homeTeam().name() == null || fdMatch.awayTeam() == null || fdMatch.awayTeam().name() == null) {
            return null;
        }

        final MatchBuilder matchBuilder = MatchBuilder.create().withTeams(fdMatch.homeTeam().name(), fdMatch.awayTeam().name());

        String groupName = fdMatch.group();
        try {
            if (StringUtils.isNotBlank(groupName)) {
                Group group = Group.valueOf(groupName);
                matchBuilder.withGroup(group);
            }
            else {
                LOG.warn("No group name for match {} vs {}. Defaulting to GROUP_A", fdMatch.homeTeam().name(), fdMatch.awayTeam().name());
                matchBuilder.withGroup(Group.GROUP_A);
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid group name '{}' for match {} vs {}. Defaulting to GROUP_A", groupName, fdMatch.homeTeam().name(), fdMatch.awayTeam().name());
            matchBuilder.withGroup(Group.GROUP_A);
        }

        matchBuilder
            .withKickOffDate(fdMatch.utcDate().toLocalDateTime())
            .withStadium(fdMatch.venue());
        return matchBuilder.build();
    }
}
