package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.ExtraPointsCalculationService;

public class ExtraBetCommand {

	private Long extraBetId;

	private Country finalWinner;
	private Country semiFinalWinner;

	private Integer points;

	private final Integer reachablePointsFinalWinner = ExtraPointsCalculationService.POINTS_FINAL_WINNER;

	private final Integer reachablePointsSemiFinalWinner = ExtraPointsCalculationService.POINTS_SEMI_FINAL_WINNER;

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

	public Integer getReachablePointsFinalWinner() {
		return reachablePointsFinalWinner;
	}

	public Integer getReachablePointsSemiFinalWinner() {
		return reachablePointsSemiFinalWinner;
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

}
