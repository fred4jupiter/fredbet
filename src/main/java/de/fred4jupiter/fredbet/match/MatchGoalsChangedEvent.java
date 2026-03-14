package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.domain.entity.Match;

/**
 * Will be triggered if a match has been finished and the goals are fixed.
 *
 * @author michael
 */
public record MatchGoalsChangedEvent(Match match) {

}
