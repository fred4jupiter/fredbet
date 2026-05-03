package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.props.CacheNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
@Transactional
public class MatchService {

    private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

    /**
     * show current K.O. matches that has been finished since 3 hours after
     * kick-off
     */
    private static final int HOURS_SHOW_UPCOMING_OTHER_MATCHES = 3;

    /**
     * show current group matches that has been finished since 2 hours after
     * kick-off
     */
    private static final int HOURS_SHOW_UPCOMING_GROUP_MATCHES = 2;

    private final MatchRepository matchRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final TeamRepository teamRepository;

    public MatchService(MatchRepository matchRepository, ApplicationEventPublisher applicationEventPublisher, TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.teamRepository = teamRepository;
    }

    public Optional<Match> findFinalMatch() {
        List<Match> matches = matchRepository.findByGroup(Group.FINAL);
        return matches.stream().findFirst();
    }

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public List<Match> findAllByOrderByKickOffDateAsc() {
        return matchRepository.findAllByOrderByKickOffDateAsc();
    }

    public Match findByMatchId(Long matchId) {
        Assert.notNull(matchId, "matchId must be given");
        return matchRepository.findById(matchId).orElseThrow(() -> new IllegalArgumentException("No match found for id: " + matchId));
    }

    public boolean hasFirstMatchStarted() {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime firstMatchKickOffDate = matchRepository.findStartDateOfFirstMatch();
        if (firstMatchKickOffDate == null) {
            return false;
        }
        return dateTimeNow.isAfter(firstMatchKickOffDate);
    }

    public boolean isBettable() {
        return !isNotBettable();
    }

    public boolean isNotBettable() {
        // if first match has started (based on kick off date) or there is no match with a result
        return hasFirstMatchStarted() || hasMatchWithResult();
    }

    public Optional<Match> findByExternalId(String externalId) {
        return matchRepository.findByExternalId(externalId);
    }

    @CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
    public Match save(Match match) {
        Team teamOne = teamRepository.findOrCreate(match.getTeamOne());
        Team teamTwo = teamRepository.findOrCreate(match.getTeamTwo());

        match.setTeamOne(teamOne);
        match.setTeamTwo(teamTwo);

        return matchRepository.save(match);
    }

    @CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
    public List<Match> saveAll(List<Match> matches) {
        return matchRepository.saveAll(matches);
    }

    public void enterMatchResult(Long matchId, Consumer<Match> consumer) {
        Match match = findMatchById(matchId);
        consumer.accept(match);

        save(match);
        publishMatchGoalsChangedEvent(match);
    }

    public void enterMatchResultsForAllMatches(Consumer<Match> consumer) {
        List<Match> allMatches = matchRepository.findAll();
        allMatches.forEach(consumer);
        matchRepository.saveAll(allMatches);
        allMatches.forEach(this::publishMatchGoalsChangedEvent);
    }

    private void publishMatchGoalsChangedEvent(Match match) {
        LOG.debug("fire MatchGoalsChangedEvent...");
        applicationEventPublisher.publishEvent(new MatchGoalsChangedEvent(match));
    }

    public List<Match> findMatchesByGroup(Group group) {
        return matchRepository.findByGroupOrderByKickOffDateAsc(group);
    }

    public List<Match> findAllMatches() {
        return matchRepository.findAllByOrderByKickOffDateAsc();
    }

    public List<Match> findAllPastMatches() {
        return matchRepository.findAllPastMatches();
    }

    public List<Match> findUpcomingMatches() {
        return matchRepository.findUpcomingMatches(HOURS_SHOW_UPCOMING_GROUP_MATCHES, HOURS_SHOW_UPCOMING_OTHER_MATCHES);
    }

    @CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
    public void deleteAllMatches() {
        matchRepository.deleteAll();
        teamRepository.deleteAll();
    }

    @CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
    public void deleteMatch(Match match) {
        Long teamOneId = match.getTeamOne().getId();
        Long teamTwoId = match.getTeamTwo().getId();
        matchRepository.deleteById(match.getId());

        deleteTeamIfNotReferencedAnymore(teamOneId);
        deleteTeamIfNotReferencedAnymore(teamTwoId);
    }

    private void deleteTeamIfNotReferencedAnymore(Long teamId) {
        if (!matchRepository.hasMatchesWithTeamId(teamId)) {
            teamRepository.deleteById(teamId);
        }
    }

    public Match findMatchById(Long matchId) {
        Optional<Match> matchOpt = matchRepository.findById(matchId);
        return matchOpt.orElse(null);
    }

    @Cacheable(CacheNames.AVAIL_GROUPS)
    public Set<Group> availableGroups() {
        LOG.info("Loading groups from DB...");
        return this.matchRepository.fetchGroupsOfAllMatches();
    }

    public List<Match> findJokerMatches(String userName) {
        return matchRepository.findMatchesOfJokerBetsForUser(userName);
    }

    public List<Match> findMatchesOfToday() {
        LocalDateTime startDateTime = LocalDate.now().atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);
        return matchRepository.findByKickOffDateBetweenOrderByKickOffDateAsc(startDateTime, endDateTime);
    }

    public List<Match> findMatchesOfYesterday() {
        LocalDateTime startDateTime = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.now().minusDays(1).atTime(23, 59, 59);
        return matchRepository.findByKickOffDateBetweenOrderByKickOffDateAsc(startDateTime, endDateTime);
    }

    public List<Match> findFinishedMatchesWithoutResult() {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(105);
        return matchRepository.findFinishedMatchesWithMissingResult(localDateTime);
    }

    public boolean isGameForThirdAvailable() {
        List<Match> matches = matchRepository.findByGroup(Group.GAME_FOR_THIRD);
        return !matches.isEmpty();
    }

    public boolean hasMatchWithResult() {
        return matchRepository.hasMatchWithResult();
    }
}
