package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;

public class ExtraBetCommand {

	private Long extraBetId;

	private Country finalWinner;
	private Country semiFinalWinner;
	private Country thirdFinalWinner;

	private Integer points;

	private Match finalMatch;

	private boolean bettable;

	public Country getFinalWinner() {
		return finalWinner;
	}

	public void setFinalWinner(Country finalWinner) {
		this.finalWinner = finalWinner;
	}

	public Country getSemiFinalWinner() {
		return semiFinalWinner;
	}

	public void setSemiFinalWinner(Country semiFinalWinner) {
		this.semiFinalWinner = semiFinalWinner;
	}

	public Long getExtraBetId() {
		return extraBetId;
	}

	public void setExtraBetId(Long extraBetId) {
		this.extraBetId = extraBetId;
	}

	public Integer getPoints() {
		if (finalMatch == null) {
			return 0;
		}

		if (finalMatch.hasResultSet() && points == null) {
			return 0;
		}
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Match getFinalMatch() {
		return finalMatch;
	}

	public void setFinalMatch(Match finalMatch) {
		this.finalMatch = finalMatch;
	}

	public boolean isBettable() {
		return bettable;
	}

	public void setBettable(boolean bettable) {
		this.bettable = bettable;
	}

	public Country getThirdFinalWinner() {
		return thirdFinalWinner;
	}

	public void setThirdFinalWinner(Country thirdFinalWinner) {
		this.thirdFinalWinner = thirdFinalWinner;
	}

}
