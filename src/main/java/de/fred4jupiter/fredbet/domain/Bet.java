package de.fred4jupiter.fredbet.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.props.FredbetConstants;

@Entity
@Table(name = "BET")
public class Bet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BET_ID")
	private Long id;

	@Column(name = "USER_NAME")
	private String userName;

	@ManyToOne(targetEntity = Match.class)
	@JoinColumn(name = "MATCH_ID")
	private Match match;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	private Integer points = Integer.valueOf(0);

	@Column(name = "PENALTY_WINNER_ONE")
	private boolean penaltyWinnerOne;

	public Integer getGoalDifference() {
		if (goalsTeamOne == null || goalsTeamTwo == null) {
			throw new IllegalStateException("Bet not finished! No goal bets set!");
		}
		return Math.abs(goalsTeamOne.intValue() - goalsTeamTwo.intValue());
	}

	public boolean isTeamOneWinner() {
		if (goalsTeamOne == null || goalsTeamTwo == null) {
			throw new IllegalStateException("Bet not finished! No goal bets set!");
		}

		return goalsTeamOne.intValue() > goalsTeamTwo.intValue();
	}

	public boolean isTeamTwoWinner() {
		if (goalsTeamOne == null || goalsTeamTwo == null) {
			throw new IllegalStateException("Bet not finished! No goal bets set!");
		}

		return goalsTeamTwo.intValue() > goalsTeamOne.intValue();
	}
	
	public boolean isUndecidedBetting() {
		return getGoalDifference().intValue() == 0;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Bet bet = (Bet) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, bet.id);
		builder.append(userName, bet.userName);
		builder.append(match, bet.match);
		builder.append(goalsTeamOne, bet.goalsTeamOne);
		builder.append(goalsTeamTwo, bet.goalsTeamTwo);
		builder.append(points, bet.points);
		builder.append(penaltyWinnerOne, bet.penaltyWinnerOne);
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(userName);
		builder.append(match);
		builder.append(goalsTeamOne);
		builder.append(goalsTeamTwo);
		builder.append(points);
		builder.append(penaltyWinnerOne);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("userName", userName);
		builder.append("match", match);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		builder.append("points", points);
		builder.append("penaltyWinnerOne", penaltyWinnerOne);
		return builder.toString();
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public Long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public Match getMatch() {
		return match;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public boolean isPenaltyWinnerOne() {
		return penaltyWinnerOne;
	}
	
	private boolean isGroupMatch() {
		return this.match.isGroupMatch();
	}

	public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
		this.penaltyWinnerOne = penaltyWinnerOne;
	}

	public String getCssClassPenaltyWinnerOne() {
		if (this.isGroupMatch() || !this.isUndecidedBetting()) {
			return "";
		}
		return this.isPenaltyWinnerOne() ? FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS : "";
	}

	public String getCssClassPenaltyWinnerTwo() {
		if (this.isGroupMatch() || !this.isUndecidedBetting()) {
			return "";
		}
		return !this.isPenaltyWinnerOne() ? FredbetConstants.BADGE_PENALTY_WINNER_BET_CSS_CLASS : "";
	}
}
