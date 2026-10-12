package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.CrestsDownloader;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.TeamRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.teambundle.TeamBundleProvider;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    public static final String FALLBACK_TEAM_NAME = "Not yet defined";

    private final TeamRepository teamRepository;

    private final CrestsCountryResolver crestsCountryResolver;

    private final CrestsDownloader crestsDownloader;

    private final CrestPlaceholderLoader crestPlaceholderLoader;

    private final MessageSourceUtil messageSourceUtil;

    private final MatchRepository matchRepository;

    private final ExtraBetRepository extraBetRepository;

    private final TeamBundleProvider teamBundleProvider;

    public TeamService(TeamRepository teamRepository, CrestsCountryResolver crestsCountryResolver,
                       CrestsDownloader crestsDownloader, CrestPlaceholderLoader crestPlaceholderLoader,
                       MessageSourceUtil messageSourceUtil, MatchRepository matchRepository, ExtraBetRepository extraBetRepository,
                       TeamBundleProvider teamBundleProvider) {
        this.teamRepository = teamRepository;
        this.crestsCountryResolver = crestsCountryResolver;
        this.crestsDownloader = crestsDownloader;
        this.crestPlaceholderLoader = crestPlaceholderLoader;
        this.messageSourceUtil = messageSourceUtil;
        this.matchRepository = matchRepository;
        this.extraBetRepository = extraBetRepository;
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

        // a stored crest wins, so that crests uploaded for country teams are shown as well
        if (StringUtils.isNotBlank(team.getSvgContent())) {
            return new SvgImage(team.getSvgContent(), team.getVersion());
        }

        if (team.getCountry() != null) {
            return crestsCountryResolver.loadCrestsImageFor(team.getCountry());
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

    /**
     * All teams saved in the database that can be chosen in a selection, i.e. without the fallback placeholder team.
     */
    public List<Team> findAllSelectableTeams() {
        return findAllTeams().stream().filter(team -> !isFallbackTeam(team)).toList();
    }

    private boolean isFallbackTeam(Team team) {
        return team.getCountry() == null && FALLBACK_TEAM_NAME.equals(team.getName());
    }

    public List<Team> findAllTeams() {
        return teamRepository.findAll().stream().sorted(new TeamSortComparator(messageSourceUtil)).toList();
    }

    public List<Country> getAllCountriesSorted() {
        Locale locale = LocaleContextHolder.getLocale();
        return Arrays.stream(Country.values())
            .sorted(Comparator.comparing(country -> messageSourceUtil.getCountryName(country, locale)))
            .toList();
    }

    /**
     * Creates a new team, either for a country or with a team name. If no crest is given, the default crest of
     * the country (or the placeholder) is used.
     *
     * @param crestSvg optional SVG content of the crest
     */
    public Team createTeam(Country country, String teamName, byte[] crestSvg) {
        Team team = country != null ? new Team(country) : new Team(teamName);
        return saveTeam(team, country, teamName, crestSvg, true);
    }

    /**
     * Updates an existing team. If no crest is given, the stored crest is kept unless the country has changed.
     *
     * @param crestSvg optional SVG content of the crest
     */
    public Team updateTeam(Long teamId, Country country, String teamName, byte[] crestSvg) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Could not find team with id=" + teamId));
        boolean countryChanged = !Objects.equals(team.getCountry(), country);
        return saveTeam(team, country, teamName, crestSvg, countryChanged);
    }

    private Team saveTeam(Team team, Country country, String teamName, byte[] crestSvg, boolean resetCrest) {
        if (country == null && StringUtils.isBlank(teamName)) {
            throw new IllegalArgumentException("Either country or team name must be given!");
        }
        if (crestSvg != null && !SvgCrestValidator.isValidSvg(crestSvg)) {
            throw new InvalidCrestException("Given crest is no valid SVG image!");
        }

        String name = country != null ? null : teamName.trim();
        checkTeamIsUnique(team.getId(), country, name);

        team.setCountry(country);
        team.setName(name);

        if (crestSvg != null) {
            team.setSvgContent(new SvgImage(new String(crestSvg, StandardCharsets.UTF_8)));
        } else if (resetCrest || StringUtils.isBlank(team.getSvgContent())) {
            team.setSvgContent(loadDefaultCrest(country));
        }

        return teamRepository.save(team);
    }

    /**
     * Team bundles that can be used to create teams. FOOTBALL_DATA_USAGE is excluded, because these teams
     * will be created by the football-data.org integration.
     */
    public List<TeamBundle> getCreatableTeamBundles() {
        return Arrays.stream(TeamBundle.values()).filter(teamBundle -> !TeamBundle.FOOTBALL_DATA_USAGE.equals(teamBundle)).toList();
    }

    /**
     * Creates a team for each country of the given team bundle. Countries that already have a team are skipped.
     *
     * @return number of newly created teams
     */
    public int createTeamsOfBundle(TeamBundle teamBundle) {
        if (teamBundle == null || TeamBundle.FOOTBALL_DATA_USAGE.equals(teamBundle)) {
            throw new IllegalArgumentException("Teams cannot be created for team bundle " + teamBundle);
        }

        Set<Country> existingCountries = teamRepository.findAll().stream()
            .map(Team::getCountry)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<Team> newTeams = teamBundleProvider.getTeams(teamBundle).stream()
            .distinct()
            .filter(country -> !existingCountries.contains(country))
            .map(country -> {
                Team team = new Team(country);
                team.setSvgContent(loadDefaultCrest(country));
                return team;
            })
            .toList();

        teamRepository.saveAll(newTeams);
        return newTeams.size();
    }

    /**
     * A team can only be deleted if it is neither used in a match nor in an extra bet.
     */
    public boolean isDeletable(Team team) {
        return !matchRepository.existsByTeamOneOrTeamTwo(team, team)
            && !extraBetRepository.existsByFinalWinnerOrSemiFinalWinnerOrThirdFinalWinner(team, team, team);
    }

    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Could not find team with id=" + teamId));
        if (!isDeletable(team)) {
            throw new TeamInUseException("Team with id=%s is still in use!".formatted(teamId));
        }
        teamRepository.delete(team);
    }

    private void checkTeamIsUnique(Long teamId, Country country, String name) {
        Team existing = country != null ? teamRepository.findByCountry(country) : teamRepository.findByName(name);
        if (existing != null && !existing.getId().equals(teamId)) {
            throw new TeamAlreadyExistsException("Team with country=%s or name=%s already exists!".formatted(country, name));
        }
    }

    private SvgImage loadDefaultCrest(Country country) {
        if (country == null) {
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }
        return crestsCountryResolver.loadCrestsImageFor(country, false)
            .orElseGet(crestPlaceholderLoader::getCrestPlaceholderIcon);
    }

    public Team createFallbackPlaceholder() {
        Team team = findOrCreateTeam(null, FALLBACK_TEAM_NAME);
        team.setSvgContent(crestPlaceholderLoader.getCrestPlaceholderIcon());
        return team;
    }
}
