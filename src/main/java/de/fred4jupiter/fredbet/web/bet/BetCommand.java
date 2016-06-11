package de.fred4jupiter.fredbet.web.bet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class BetCommand {

	private Long betId;

	private Long matchId;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	private final MessageUtil messageUtil;

	private String redirectViewName;

	private Bet bet;

	private boolean penaltyWinnerOne;

	public BetCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	public BetCommand(MessageUtil messageUtil, Bet bet) {
		this.messageUtil = messageUtil;
		this.bet = bet;
		setBetId(bet.getId());
		setMatchId(bet.getMatch().getId());
		setGoalsTeamOne(bet.getGoalsTeamOne());
		setGoalsTeamTwo(bet.getGoalsTeamTwo());
	}
	
	public Bet getBet() {
		return bet;
	}

	public void setBet(Bet bet) {
		this.bet = bet;
		setBetId(bet.getId());
		setMatchId(bet.getMatch().getId());
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("betId", betId);
		builder.append("bet", bet);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		return builder.toString();
	}

	public boolean hasGoalsSet() {
		return goalsTeamOne != null && goalsTeamTwo != null;
	}

	public String getTeamNameOne() {
		Country countryOne = getCountryOne();
		return hasContrySet(countryOne) ? messageUtil.getCountryName(countryOne) : getMatch().getTeamNameOne();
	}

	public String getTeamNameTwo() {
		Country countryTwo = getCountryTwo();
		return hasContrySet(countryTwo) ? messageUtil.getCountryName(countryTwo) : getMatch().getTeamNameTwo();
	}

	private boolean hasContrySet(Country country) {
		return country != null && !Country.NONE.equals(country);
	}

	public Integer getGoalsTeamOne() {
		return goalsTeamOne;
	}

	public void setGoalsTeamOne(Integer goalsTeamOne) {
		this.goalsTeamOne = goalsTeamOne;
	}

	public Integer getGoalsTeamTwo() {
		return goalsTeamTwo;
	}

	public void setGoalsTeamTwo(Integer goalsTeamTwo) {
		this.goalsTeamTwo = goalsTeamTwo;
	}

	public Long getBetId() {
		return betId;
	}

	public void setBetId(Long betId) {
		this.betId = betId;
	}

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public boolean isShowCountryIcons() {
		return hasContrySet(this.getCountryOne()) && hasContrySet(this.getCountryTwo());
	}

	public String getIconPathTeamOne() {
		if (this.getCountryOne() == null) {
			return "";
		}

		return this.getCountryOne().getIconPath();
	}

	public String getIconPathTeamTwo() {
		if (this.getCountryTwo() == null) {
			return "";
		}

		return this.getCountryTwo().getIconPath();
	}

	private Country getCountryOne() {
		if (getMatch() == null) {
			return null;
		}
		return getMatch().getCountryOne();
	}

	private Match getMatch() {
		if (this.bet == null) {
			return null;
		}
		return this.bet.getMatch();
	}

	private Country getCountryTwo() {
		if (getMatch() == null) {
			return null;
		}
		return getMatch().getCountryTwo();
	}

	public String getNameTeamOne() {
		if (getMatch() == null) {
			return null;
		}
		return getMatch().getTeamNameOne();
	}

	public String getNameTeamTwo() {
		if (getMatch() == null) {
			return null;
		}
		return getMatch().getTeamNameTwo();
	}

	public String getRedirectViewName() {
		return redirectViewName;
	}

	public void setRedirectViewName(String redirectViewName) {
		this.redirectViewName = redirectViewName;
	}

	public boolean isPenaltyWinnerOne() {
		return penaltyWinnerOne;
	}

	public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
		this.penaltyWinnerOne = penaltyWinnerOne;
	}
	
	public boolean isGroupMatch() {
		return getMatch().isGroupMatch();
	}
}
