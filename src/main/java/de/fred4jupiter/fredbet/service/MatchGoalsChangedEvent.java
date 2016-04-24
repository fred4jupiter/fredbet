package de.fred4jupiter.fredbet.service;

import org.springframework.context.ApplicationEvent;

import de.fred4jupiter.fredbet.domain.Match;

/**
 * Will be triggered if a match has been finished and the goals are fixed.
 * 
 * @author michael
 *
 */
public class MatchGoalsChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = -1204081537702240481L;

	private final Match match;

	public MatchGoalsChangedEvent(Object source, Match match) {
		super(source);
		this.match = match;
	}

	public Match getMatch() {
		return match;
	}
}
