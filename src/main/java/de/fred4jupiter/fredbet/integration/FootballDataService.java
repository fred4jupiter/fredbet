package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FootballDataProperties;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class FootballDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataService.class);

    private final FredbetProperties fredbetProperties;

    private final MatchService matchService;

    private final BettingService bettingService;

    private final FdMatchConverter fdMatchConverter;

    FootballDataService(FredbetProperties fredbetProperties, MatchService matchService, BettingService bettingService,
                        FdMatchConverter fdMatchConverter) {
        this.fredbetProperties = fredbetProperties;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.fdMatchConverter = fdMatchConverter;
    }

    public int importData() {
        return importData("WC", 2026);
    }

    public int importData(String competitionCode, int season) {
        RestClient restClient = createRestClient();
        FdMatches fdMatches = restClient.get().uri("/competitions/{competitionCode}/matches", competitionCode)
            .attribute("season", season).retrieve().body(FdMatches.class);
        LOG.debug("Response from Football Data: fdMatches={}", fdMatches);
        return importMatches(fdMatches);
    }

    private @NonNull RestClient createRestClient() {
        final FootballDataProperties footballDataProperties = this.fredbetProperties.integration().footballData();
        return RestClient.builder().baseUrl(footballDataProperties.baseUrl()).defaultHeader("X-Auth-Token", footballDataProperties.apiToken()).build();
    }

    private int importMatches(FdMatches fdMatches) {
        if (fdMatches == null) {
            LOG.warn("Could not load football data fdMatchesList!");
            return 0;
        }
        LOG.debug("Importing {} Football-Data fdMatchesList", fdMatches.matches().size());

        bettingService.deleteAllBets();
        LOG.info("deleted all bets");

        matchService.deleteAllMatches();
        LOG.info("deleted all fdMatchesList");

        final List<FdMatch> fdMatchesList = fdMatches.matches();

        final List<Match> matches = fdMatchesList.stream()
            .map(fdMatchConverter::mapToMatch)
            .filter(Objects::nonNull)
            .toList();
        matchService.saveAll(matches);

        LOG.debug("imported {} matches", matches.size());
        return matches.size();
    }
}
