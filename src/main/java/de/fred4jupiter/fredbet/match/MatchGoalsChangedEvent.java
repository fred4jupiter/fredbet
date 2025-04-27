package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.domain.entity.Match;

/**
 * Will be triggered if a match has been finished and the goals are fixed.
 *
 * @author michael
 */
public class MatchGoalsChangedEvent {

    private final Match match;

    public MatchGoalsChangedEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
