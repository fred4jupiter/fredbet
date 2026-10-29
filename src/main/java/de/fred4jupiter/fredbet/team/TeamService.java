package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.crests.CrestPlaceholderLoader;
import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.SvgImage;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.integration.CrestsDownloader;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.TeamRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;
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

        // a stored crest wins, so that crests uploaded for country or club teams are shown as well
        if (StringUtils.isNotBlank(team.getSvgContent())) {
            return new SvgImage(team.getSvgContent(), team.getVersion());
        }

        if (team.getTeamBundleEntry() != null) {
            return crestsCountryResolver.loadCrestsImageFor(team.getTeamBundleEntry());
        }

        return crestPlaceholderLoader.getCrestPlaceholderIcon();
    }

    public Team findOrCreateTeam(TeamBundleEntry teamBundleEntry, String teamName) {
        return findOrCreateTeam(teamBundleEntry, teamName, _ -> {
        });
    }

    public Team findOrCreateTeam(TeamBundleEntry teamBundleEntry, String teamName, String fdTeamId) {
        return findOrCreateTeam(teamBundleEntry, teamName, newTeam -> {
            if (newTeam.getSvgContent() == null) {
                newTeam.setSvgContent(crestsDownloader.downloadCrestsByUrl(fdTeamId));
            }
        });
    }

    public Team findOrCreateTeam(TeamBundleEntry teamBundleEntry, String teamName, Consumer<Team> newTeamCallback) {
        Team team = teamRepository.findByTeamBundleEntryOrName(teamBundleEntry, StringUtils.isNotBlank(teamName) ? teamName : FALLBACK_TEAM_NAME);
        if (team != null) {
            return team;
        }

        Team newTeam = createNewTeam(teamBundleEntry, teamName);

        newTeamCallback.accept(newTeam);

        if (newTeam.getSvgContent() == null) {
            newTeam.setSvgContent(crestPlaceholderLoader.getCrestPlaceholderIcon());
        }

        return teamRepository.save(newTeam);
    }

    private @NonNull Team createNewTeam(TeamBundleEntry teamBundleEntry, String teamName) {
        if (teamBundleEntry != null) {
            Team newTeam = new Team(teamBundleEntry);
            crestsCountryResolver.loadCrestsImageFor(teamBundleEntry, false).ifPresent(newTeam::setSvgContent);
            return newTeam;
        }

        return new Team(StringUtils.isNotBlank(teamName) ? teamName : FALLBACK_TEAM_NAME);
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    /**
     * The team marked as team favourite or null, if no team is marked.
     */
    public Team findFavouriteTeam() {
        return teamRepository.findByFavouriteTrue().stream().findFirst().orElse(null);
    }

    /**
     * All teams saved in the database that can be chosen in a selection, i.e. without the fallback placeholder team.
     */
    public List<Team> findAllSelectableTeams() {
        return findAllTeams().stream().filter(team -> !isFallbackTeam(team)).toList();
    }

    /**
     * All teams that play in at least one match, without the fallback placeholder team.
     */
    public List<Team> findAllSelectableTeamsOfMatches() {
        return matchRepository.getAllTeamsOfMatches().stream()
            .filter(team -> !isFallbackTeam(team))
            .sorted(new TeamSortComparator(messageSourceUtil))
            .toList();
    }

    private boolean isFallbackTeam(Team team) {
        return team.getTeamBundleEntry() == null && FALLBACK_TEAM_NAME.equals(team.getName());
    }

    public List<Team> findAllTeams() {
        return teamRepository.findAll().stream().sorted(new TeamSortComparator(messageSourceUtil)).toList();
    }

    public List<TeamBundleEntry> getAllTeamBundleEntriesSorted() {
        Locale locale = LocaleContextHolder.getLocale();
        return Arrays.stream(TeamBundleEntryType.values())
            .flatMap(teamBundleEntryType -> teamBundleEntryType.getEntries().stream())
            .sorted(Comparator.comparing(teamBundleEntry -> messageSourceUtil.getTeamBundleEntryName(teamBundleEntry, locale)))
            .toList();
    }

    /**
     * Creates a new team, either for a team bundle entry (country or club) or with a team name. If no crest is
     * given, the default crest of the entry (or the placeholder) is used.
     *
     * @param favourite whether the team is marked as team favourite (removes the flag from all other teams)
     * @param crestImage optional crest as SVG, JPEG or PNG image (JPEG and PNG are wrapped into an SVG)
     */
    public Team createTeam(TeamBundleEntry teamBundleEntry, String teamName, boolean favourite, byte[] crestImage) {
        Team team = teamBundleEntry != null ? new Team(teamBundleEntry) : new Team(teamName);
        return saveTeam(team, teamBundleEntry, teamName, favourite, crestImage, true);
    }

    /**
     * Updates an existing team. If no crest is given, the stored crest is kept unless the team bundle entry has changed.
     *
     * @param favourite whether the team is marked as team favourite (removes the flag from all other teams)
     * @param crestImage optional crest as SVG, JPEG or PNG image (JPEG and PNG are wrapped into an SVG)
     */
    public Team updateTeam(Long teamId, TeamBundleEntry teamBundleEntry, String teamName, boolean favourite, byte[] crestImage) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Could not find team with id=" + teamId));
        boolean teamBundleEntryChanged = !Objects.equals(team.getTeamBundleEntry(), teamBundleEntry);
        return saveTeam(team, teamBundleEntry, teamName, favourite, crestImage, teamBundleEntryChanged);
    }

    private Team saveTeam(Team team, TeamBundleEntry teamBundleEntry, String teamName, boolean favourite, byte[] crestImage, boolean resetCrest) {
        if (teamBundleEntry == null && StringUtils.isBlank(teamName)) {
            throw new IllegalArgumentException("Either team bundle entry or team name must be given!");
        }
        String crestSvg = toCrestSvg(crestImage);

        String name = teamBundleEntry != null ? null : teamName.trim();
        checkTeamIsUnique(team.getId(), teamBundleEntry, name);

        team.setTeamBundleEntry(teamBundleEntry);
        team.setName(name);
        if (favourite) {
            clearOtherFavourites(team.getId());
        }
        team.setFavourite(favourite);

        if (crestSvg != null) {
            team.setSvgContent(new SvgImage(crestSvg));
        } else if (resetCrest || StringUtils.isBlank(team.getSvgContent())) {
            team.setSvgContent(loadDefaultCrest(teamBundleEntry));
        }

        return teamRepository.save(team);
    }

    /**
     * Team bundles that can be used to create teams.
     */
    public List<TeamBundle> getCreatableTeamBundles() {
        return List.of(TeamBundle.values());
    }

    /**
     * Creates a team for each entry of the given team bundle. Entries that already have a team are skipped.
     *
     * @return number of newly created teams
     */
    public int createTeamsOfBundle(TeamBundle teamBundle) {
        if (teamBundle == null) {
            throw new IllegalArgumentException("Teams cannot be created without a team bundle!");
        }

        Set<TeamBundleEntry> existingEntries = teamRepository.findAll().stream()
            .map(Team::getTeamBundleEntry)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<Team> newTeams = teamBundleProvider.getTeams(teamBundle).stream()
            .distinct()
            .filter(teamBundleEntry -> !existingEntries.contains(teamBundleEntry))
            .map(teamBundleEntry -> {
                Team team = new Team(teamBundleEntry);
                team.setSvgContent(loadDefaultCrest(teamBundleEntry));
                return team;
            })
            .toList();

        teamRepository.saveAll(newTeams);
        return newTeams.size();
    }

    /**
     * Counts in how many matches and extra bets the given team is used.
     */
    public TeamUsage getTeamUsage(Team team) {
        long matches = matchRepository.countByTeamOneOrTeamTwo(team, team);
        long extraBets = extraBetRepository.countByFinalWinnerOrSemiFinalWinnerOrThirdFinalWinner(team, team, team);
        return new TeamUsage(matches, extraBets);
    }

    /**
     * Deletes the team. A team that is still used in a match or an extra bet cannot be deleted.
     *
     * @throws TeamInUseException if the team is still used
     */
    public void deleteTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Could not find team with id=" + teamId));
        TeamUsage teamUsage = getTeamUsage(team);
        if (teamUsage.isUsed()) {
            throw new TeamInUseException(teamId, teamUsage);
        }
        teamRepository.delete(team);
    }

    /**
     * @throws InvalidCrestException if the given crest is no valid SVG, JPEG or PNG image
     */
    private String toCrestSvg(byte[] crestImage) {
        if (crestImage == null) {
            return null;
        }
        if (RasterCrestConverter.isRasterImage(crestImage)) {
            return RasterCrestConverter.toSvg(crestImage);
        }
        if (!SvgCrestValidator.isValidSvg(crestImage)) {
            throw new InvalidCrestException("Given crest is no valid SVG image!");
        }
        return new String(crestImage, StandardCharsets.UTF_8);
    }

    /**
     * Deletes all teams that are not used in a match or an extra bet. Teams that are still used are kept.
     */
    public TeamDeletionResult deleteAllUnusedTeams() {
        List<Team> allTeams = teamRepository.findAll();
        List<Team> unusedTeams = allTeams.stream().filter(team -> !getTeamUsage(team).isUsed()).toList();
        teamRepository.deleteAll(unusedTeams);
        return new TeamDeletionResult(unusedTeams.size(), allTeams.size() - unusedTeams.size());
    }

    /**
     * Only one team can be the team favourite, so the flag is removed from all other teams.
     */
    private void clearOtherFavourites(Long teamId) {
        teamRepository.findByFavouriteTrue().stream()
            .filter(other -> !other.getId().equals(teamId))
            .forEach(other -> other.setFavourite(false));
    }

    private void checkTeamIsUnique(Long teamId, TeamBundleEntry teamBundleEntry, String name) {
        Team existing = teamBundleEntry != null ? teamRepository.findByTeamBundleEntry(teamBundleEntry) : teamRepository.findByName(name);
        if (existing != null && !existing.getId().equals(teamId)) {
            throw new TeamAlreadyExistsException("Team with teamBundleEntry=%s or name=%s already exists!".formatted(teamBundleEntry, name));
        }
    }

    private SvgImage loadDefaultCrest(TeamBundleEntry teamBundleEntry) {
        if (teamBundleEntry == null) {
            return crestPlaceholderLoader.getCrestPlaceholderIcon();
        }
        return crestsCountryResolver.loadCrestsImageFor(teamBundleEntry, false)
            .orElseGet(crestPlaceholderLoader::getCrestPlaceholderIcon);
    }

    public Team createFallbackPlaceholder() {
        Team team = findOrCreateTeam(null, FALLBACK_TEAM_NAME);
        team.setSvgContent(crestPlaceholderLoader.getCrestPlaceholderIcon());
        return team;
    }

    public void deleteAllTeams() {
        teamRepository.deleteAll();
    }
}
