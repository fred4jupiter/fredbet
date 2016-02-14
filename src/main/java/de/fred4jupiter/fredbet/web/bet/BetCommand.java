package de.fred4jupiter.fredbet.web.bet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class BetCommand {

	private Long betId;

	private Long matchId;

	private Country countryTeamOne;
	private Country countryTeamTwo;

	private String nameTeamOne;
	private String nameTeamTwo;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	private MessageUtil messageUtil;
	
	private String redirectViewName;

	public BetCommand() {
	}

	public BetCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	public void setMessageUtil(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("betId", betId);
		builder.append("countryTeamOne", countryTeamOne);
		builder.append("countryTeamTwo", countryTeamTwo);
		builder.append("nameTeamOne", nameTeamOne);
		builder.append("nameTeamTwo", nameTeamTwo);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		return builder.toString();
	}

	public boolean hasGoalsSet() {
		return goalsTeamOne != null && goalsTeamTwo != null;
	}

	public String getTeamNameOne() {
		return !Country.NONE.equals(this.countryTeamOne) ? messageUtil.getCountryName(countryTeamOne) : nameTeamOne;
	}

	public String getTeamNameTwo() {
		return !Country.NONE.equals(this.countryTeamTwo) ? messageUtil.getCountryName(countryTeamTwo) : nameTeamTwo;
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
		return !Country.NONE.equals(this.countryTeamOne) && !Country.NONE.equals(this.countryTeamTwo);
	}

	public String getIconPathTeamOne() {
		if (this.countryTeamOne == null) {
			return "";
		}

		return this.countryTeamOne.getIconPath();
	}

	public String getIconPathTeamTwo() {
		if (this.countryTeamTwo == null) {
			return "";
		}

		return this.countryTeamTwo.getIconPath();
	}

	public Country getCountryTeamOne() {
		return countryTeamOne;
	}

	public void setCountryTeamOne(Country countryTeamOne) {
		this.countryTeamOne = countryTeamOne;
	}

	public Country getCountryTeamTwo() {
		return countryTeamTwo;
	}

	public void setCountryTeamTwo(Country countryTeamTwo) {
		this.countryTeamTwo = countryTeamTwo;
	}

	public String getNameTeamOne() {
		return nameTeamOne;
	}

	public void setNameTeamOne(String nameTeamOne) {
		this.nameTeamOne = nameTeamOne;
	}

	public String getNameTeamTwo() {
		return nameTeamTwo;
	}

	public void setNameTeamTwo(String nameTeamTwo) {
		this.nameTeamTwo = nameTeamTwo;
	}

    public String getRedirectViewName() {
        return redirectViewName;
    }

    public void setRedirectViewName(String redirectViewName) {
        this.redirectViewName = redirectViewName;
    }
}
