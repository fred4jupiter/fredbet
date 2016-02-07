package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class MatchCommand {

	private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

	private String matchId;

	private Country countryTeamOne;
	private Country countryTeamTwo;

	private String nameTeamOne;
	private String nameTeamTwo;

	private Integer teamResultOne;

	private Integer teamResultTwo;

	private Group group;

	private LocalDateTime kickOffDate;

	private String stadium;

	private Integer userBetGoalsTeamOne;

	private Integer userBetGoalsTeamTwo;

	private Integer points;

	private boolean deletable;

	private MessageUtil messageUtil;

	public MatchCommand() {
	}

	public MatchCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	public void setMessageUtil(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	private boolean hasResults() {
		return teamResultOne != null && teamResultTwo != null;
	}

	public boolean isBettable() {
		if (hasMatchStarted() || hasMatchFinished() || hasResults()) {
			return false;
		}

		return true;
	}

	private boolean hasMatchStarted() {
		return LocalDateTime.now().isAfter(getKickOffDate());
	}

	public boolean hasMatchFinished() {
		return teamResultOne != null && teamResultTwo != null;
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

	public String getTeamNameOne() {
		return !Country.NONE.equals(this.countryTeamOne) ? messageUtil.getCountryName(countryTeamOne) : nameTeamOne;
	}

	public String getTeamNameTwo() {
		return !Country.NONE.equals(this.countryTeamTwo) ? messageUtil.getCountryName(countryTeamTwo) : nameTeamTwo;
	}

	public Integer getTeamResultOne() {
		return teamResultOne;
	}

	public void setTeamResultOne(Integer teamResultOne) {
		this.teamResultOne = teamResultOne;
	}

	public Integer getTeamResultTwo() {
		return teamResultTwo;
	}

	public void setTeamResultTwo(Integer teamResultTwo) {
		this.teamResultTwo = teamResultTwo;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("countryTeamOne", countryTeamOne);
		builder.append("countryTeamTwo", countryTeamTwo);
		builder.append("nameTeamOne", nameTeamOne);
		builder.append("nameTeamTwo", nameTeamTwo);
		builder.append("teamResultOne", teamResultOne);
		builder.append("teamResultTwo", teamResultTwo);
		builder.append("group", group);
		builder.append("kickOffDate", getKickOffDate());
		return builder.toString();
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Integer getUserBetGoalsTeamOne() {
		return userBetGoalsTeamOne;
	}

	public void setUserBetGoalsTeamOne(Integer userBetgoalsTeamOne) {
		this.userBetGoalsTeamOne = userBetgoalsTeamOne;
	}

	public Integer getUserBetGoalsTeamTwo() {
		return userBetGoalsTeamTwo;
	}

	public void setUserBetGoalsTeamTwo(Integer userBetGoalsTeamTwo) {
		this.userBetGoalsTeamTwo = userBetGoalsTeamTwo;
	}

	public Integer getPoints() {
		if (hasMatchFinished() && points == null) {
			return Integer.valueOf(0);
		}
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public boolean isDateOrTimeEmpty() {
		if (kickOffDate == null) {
			return true;
		}

		return false;
	}

	public LocalDateTime getKickOffDate() {
		return kickOffDate;
	}

	public void setKickOffDate(LocalDateTime kickOffDate) {
		this.kickOffDate = kickOffDate;
	}

	public String getKickOffDateString() {
		if (kickOffDate == null) {
			return "";
		}
		return kickOffDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
	}

	public void setKickOffDateString(String kickOffDateString) {
		if (StringUtils.isBlank(kickOffDateString)) {
			return;
		}
		this.kickOffDate = LocalDateTime.parse(kickOffDateString, DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
	}

	public boolean isOnlyOneResultSet() {
		return (getTeamResultOne() == null && getTeamResultTwo() != null) || (getTeamResultOne() != null && getTeamResultTwo() == null);
	}

	public boolean hasValidGoals() {
		return (getTeamResultOne() != null && getTeamResultOne().intValue() < 0)
				|| (getTeamResultTwo() != null && getTeamResultTwo().intValue() < 0);
	}

	public boolean isTeamNamesEmpty() {
		return (countryTeamOne == null && countryTeamTwo == null) && (StringUtils.isBlank(nameTeamOne) && StringUtils.isBlank(nameTeamTwo));
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
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

}
