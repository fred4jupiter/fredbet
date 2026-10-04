package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.CrestsDownloader;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.TeamRepository;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.teambundle.TeamBundleProvider;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
public class TeamService {

    public static final String FALLBACK_TEAM_NAME = "Not yet defined";

    private final TeamRepository teamRepository;

    private final CrestsCountryResolver crestsCountryResolver;

    private final CrestsDownloader crestsDownloader;

    private final CrestPlaceholderLoader crestPlaceholderLoader;

    private final MessageSourceUtil messageSourceUtil;

    private final RuntimeSettingsService runtimeSettingsService;

    private final MatchRepository matchRepository;

    private final TeamBundleProvider teamBundleProvider;

    public TeamService(TeamRepository teamRepository, CrestsCountryResolver crestsCountryResolver,
                       CrestsDownloader crestsDownloader, CrestPlaceholderLoader crestPlaceholderLoader,
                       MessageSourceUtil messageSourceUtil, RuntimeSettingsService runtimeSettingsService, MatchRepository matchRepository, TeamBundleProvider teamBundleProvider) {
        this.teamRepository = teamRepository;
        this.crestsCountryResolver = crestsCountryResolver;
        this.crestsDownloader = crestsDownloader;
        this.crestPlaceholderLoader = crestPlaceholderLoader;
        this.messageSourceUtil = messageSourceUtil;
        this.runtimeSettingsService = runtimeSettingsService;
        this.matchRepository = matchRepository;
        this.teamBundleProvider = teamBundleProvider;
    }

    public SvgImage loadCrestImage(Long teamId) {
        if (teamId == null) {
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }

        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isEmpty()) {
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }

        final Team team = teamOpt.get();

        if (team.getCountry() != null) {
            return crestsCountryResolver.loadCrestsImageFor(team.getCountry());
        }

        if (StringUtils.isNotBlank(team.getSvgContent())) {
            return new SvgImage(team.getSvgContent(), team.getVersion());
        }

        return crestPlaceholderLoader.getCrestPlaceholderIcon();
    }

    public Team findOrCreateTeam(Country country, String teamName) {
        return findOrCreateTeam(country, teamName, _ -> {
        });
    }

    public Team findOrCreateTeam(Country country, String teamName, String fdTeamId) {
        return findOrCreateTeam(country, teamName, newTeam -> {
            if (newTeam.getSvgContent() == null) {
                newTeam.setSvgContent(crestsDownloader.downloadCrestsByUrl(fdTeamId));
            }
        });
    }

    public Team findOrCreateTeam(Country country, String teamName, Consumer<Team> newTeamCallback) {
        Team team = teamRepository.findByCountryOrName(country, StringUtils.isNotBlank(teamName) ? teamName : FALLBACK_TEAM_NAME);
        if (team != null) {
            return team;
        }

        Team newTeam = createNewTeam(country, teamName);

        newTeamCallback.accept(newTeam);

        if (newTeam.getSvgContent() == null) {
            newTeam.setSvgContent(crestPlaceholderLoader.getCrestPlaceholderIcon());
        }

        return teamRepository.save(newTeam);
    }

    private @NonNull Team createNewTeam(Country country, String teamName) {
        if (country != null) {
            Team newTeam = new Team(country);
            crestsCountryResolver.loadCrestsImageFor(country, false).ifPresent(newTeam::setSvgContent);
            return newTeam;
        }

        return new Team(StringUtils.isNotBlank(teamName) ? teamName : FALLBACK_TEAM_NAME);
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    public List<Team> getAllTeamsOfMatches() {
        final TeamBundle teamBundle = runtimeSettingsService.loadRuntimeSettings().getTeamBundle();
        if (!TeamBundle.FOOTBALL_DATA_USAGE.equals(teamBundle)) {
            // load predefined teams from team bundle
            List<Country> allPossibleCountries = teamBundleProvider.getTeams(teamBundle);
            return allPossibleCountries.stream()
                .map(Team::new)
                .sorted(new TeamSortComparator(messageSourceUtil))
                .toList();
        }

        // load teams from existent matches
        List<Team> allTeamsOfMatches = matchRepository.getAllTeamsOfMatches();
        return allTeamsOfMatches.stream().sorted(new TeamSortComparator(messageSourceUtil)).toList();
    }

    public Team createFallbackPlaceholder() {
        Team team = findOrCreateTeam(null, FALLBACK_TEAM_NAME);
        team.setSvgContent(crestPlaceholderLoader.getCrestPlaceholderIcon());
        return team;
    }
}
