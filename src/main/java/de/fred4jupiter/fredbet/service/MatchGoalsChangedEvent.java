package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.Match;
import org.springframework.context.ApplicationEvent;

/**
 * Will be triggered if a match has been finished and the goals are fixed.
 *
 * @author michael
 */
public class MatchGoalsChangedEvent extends ApplicationEvent {

    private final Match match;

    public MatchGoalsChangedEvent(Object source, Match match) {
        super(source);
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
