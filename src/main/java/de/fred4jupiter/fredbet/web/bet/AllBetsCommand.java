package de.fred4jupiter.fredbet.web.bet;

import java.util.List;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.web.AbstractMatchHeaderCommand;

public class AllBetsCommand extends AbstractMatchHeaderCommand {

	private List<Bet> allBetsForMatch;

	private Match match;

	public List<Bet> getAllBetsForMatch() {
		return allBetsForMatch;
	}

	public Long getMatchId() {
		return this.match.getId();
	}

	public Match getMatch() {
		return match;
	}

	public void setAllBetsForMatch(List<Bet> allBetsForMatch) {
		this.allBetsForMatch = allBetsForMatch;
	}

	public void setMatch(Match match) {
		this.match = match;
	}
}
