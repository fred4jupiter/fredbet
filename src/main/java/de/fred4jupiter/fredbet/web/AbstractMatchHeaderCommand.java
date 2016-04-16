package de.fred4jupiter.fredbet.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;

public abstract class AbstractMatchHeaderCommand {

	private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";

	protected Country countryTeamOne;
	protected Country countryTeamTwo;

	protected LocalDateTime kickOffDate;

	protected String nameTeamOne;
	protected String nameTeamTwo;

	protected Group group;

	protected String stadium;

	protected final MessageUtil messageUtil;

	public AbstractMatchHeaderCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	public boolean hasMatchStarted() {
		return LocalDateTime.now().isAfter(getKickOffDate());
	}

	public boolean isShowCountryIcons() {
		if (this.countryTeamOne == null || this.countryTeamTwo == null) {
			return false;
		}
		return isNotBlank(countryTeamOne) && isNotBlank(countryTeamTwo);
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
		if (this.countryTeamOne == null) {
			return nameTeamOne;
		}
		return isNotBlank(countryTeamOne) ? messageUtil.getCountryName(countryTeamOne) : nameTeamOne;
	}

	public String getTeamNameTwo() {
		if (this.countryTeamTwo == null) {
			return nameTeamTwo;
		}
		return isNotBlank(countryTeamTwo) ? messageUtil.getCountryName(countryTeamTwo) : nameTeamTwo;
	}

	protected boolean isBlank(Country country) {
		return !isNotBlank(country);
	}

	protected boolean isNotBlank(Country country) {
		if (country == null) {
			return false;
		}

		if (Country.NONE.equals(country)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("countryTeamOne", countryTeamOne);
		builder.append("countryTeamTwo", countryTeamTwo);
		builder.append("nameTeamOne", nameTeamOne);
		builder.append("nameTeamTwo", nameTeamTwo);
		return builder.toString();
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

	public boolean hasCountriesAndTeamNamesEntered() {
		return ((isNotBlank(countryTeamOne) || isNotBlank(countryTeamTwo))
				&& (StringUtils.isNotBlank(nameTeamOne) || StringUtils.isNotBlank(nameTeamTwo)));
	}

	public boolean hasSameTeamsPlayingAgainstEachOther() {
		if (isNotBlank(countryTeamOne) && isNotBlank(countryTeamTwo)) {
			if (countryTeamOne.equals(countryTeamTwo)) {
				return true;
			}
		}

		if (StringUtils.isNotBlank(nameTeamOne) && StringUtils.isNotBlank(nameTeamTwo)) {
			if (nameTeamOne.equals(nameTeamTwo)) {
				return true;
			}
		}

		return false;
	}
}
