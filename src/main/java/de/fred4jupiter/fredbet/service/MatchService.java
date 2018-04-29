package de.fred4jupiter.fredbet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.props.CacheNames;
import de.fred4jupiter.fredbet.repository.MatchRepository;

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

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public List<Match> findAll() {
		return matchRepository.findAll();
	}

	public Match findByMatchId(Long matchId) {
		Assert.notNull(matchId, "matchId must be given");
		return matchRepository.getOne(matchId);
	}

	@CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
	public Match save(Match match) {
		match = matchRepository.save(match);

		if (match.hasGoalsChanged()) {
			LOG.debug("fire MatchGoalsChangedEvent...");
			applicationEventPublisher.publishEvent(new MatchGoalsChangedEvent(this, match));
		}

		return match;
	}

	public List<Match> findMatchesByGroup(Group group) {
		return matchRepository.findByGroupOrderByKickOffDateAsc(group);
	}

	public List<Match> findAllMatches() {
		return matchRepository.findAllByOrderByKickOffDateAsc();
	}

	public List<Match> findUpcomingMatches() {
		LocalDateTime groupKickOffBeginSelectionDate = LocalDateTime.now().minusHours(HOURS_SHOW_UPCOMING_GROUP_MATCHES);
		LocalDateTime koKickOffBeginSelectionDate = LocalDateTime.now().minusHours(HOURS_SHOW_UPCOMING_OTHER_MATCHES);
		return matchRepository.findUpcomingMatches(groupKickOffBeginSelectionDate, koKickOffBeginSelectionDate);
	}

	@CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
	public void deleteAllMatches() {
		matchRepository.deleteAll();
	}

	@CacheEvict(cacheNames = CacheNames.AVAIL_GROUPS, allEntries = true)
	public void deleteMatch(Long matchId) {
		matchRepository.deleteById(matchId);
	}

	public Match findMatchById(Long matchId) {
		Optional<Match> matchOpt = matchRepository.findById(matchId);
		return matchOpt.isPresent() ? matchOpt.get() : null;
	}

	@Cacheable(CacheNames.AVAIL_GROUPS)
	public Set<Group> availableGroups() {
		LOG.info("Loading groups from DB...");
		return this.matchRepository.fetchGroupsOfAllMatches();
	}
}
