package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.imexport.MatchBusinessKey;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Table(name = "MATCHES")
public class Match implements MatchResult, MatchBusinessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATCH_ID")
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "COUNTRY_ONE")),
            @AttributeOverride(name = "name", column = @Column(name = "TEAM_NAME_ONE")),
            @AttributeOverride(name = "goals", column = @Column(name = "GOALS_TEAM_ONE"))
    })
    private Team teamOne;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "COUNTRY_TWO")),
            @AttributeOverride(name = "name", column = @Column(name = "TEAM_NAME_TWO")),
            @AttributeOverride(name = "goals", column = @Column(name = "GOALS_TEAM_TWO"))
    })
    private Team teamTwo;

    @Enumerated(EnumType.STRING)
    @Column(name = "MATCH_GROUP")
    private Group group;

    @Column(name = "PENALTY_WINNER_ONE")
    private boolean penaltyWinnerOne;

    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    @Column(name = "KICK_OFF_DATE")
    private LocalDateTime kickOffDate;

    @Column(name = "STADIUM")
    private String stadium;

    @Transient
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public boolean hasCountriesSet() {
        return teamOne.hasCountrySet() && teamTwo.hasCountrySet();
    }

    public boolean hasResultSet() {
        return teamOne.hasResultSet() && teamTwo.hasResultSet();
    }

    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(kickOffDate);
    }

    public Integer getGoalDifference() {
        if (teamOne.getGoals() == null || teamTwo.getGoals() == null) {
            throw new IllegalStateException("Match has not finished! No goal results set!");
        }
        return Math.abs(teamOne.getGoals() - teamTwo.getGoals());
    }

    /**
     * Goal difference is 0, e.g. 1:1 or 3:3
     *
     * @return
     */
    @Override
    public boolean isUndecidedResult() {
        if (teamOne.getGoals() == null || teamTwo.getGoals() == null) {
            return false;
        }
        return getGoalDifference() == 0;
    }

    @Override
    public boolean isTeamOneWinner() {
        return isFirstWinner(teamOne, teamTwo);
    }

    @Override
    public boolean isTeamTwoWinner() {
        return isFirstWinner(teamTwo, teamOne);
    }

    private boolean isFirstWinner(Team teamOne, Team teamTwo) {
        if (teamOne.getGoals() == null || teamTwo.getGoals() == null) {
            throw new IllegalStateException("Match has not finished! No goal results set!");
        }

        if (getGoalDifference() == 0) {
            if (isGroupMatch()) {
                return false;
            } else {
                // check penalty winner
                return (this.teamOne.equals(teamOne) && penaltyWinnerOne) || (this.teamTwo.equals(teamOne) && !penaltyWinnerOne);
            }
        }

        return teamOne.getGoals() > teamTwo.getGoals();
    }

    public Country getWinner() {
        if (!hasResultSet()) {
            return null;
        }

        if (isTeamOneWinner()) {
            return teamOne.getCountry();
        }

        if (isTeamTwoWinner()) {
            return teamTwo.getCountry();
        }

        return null;
    }

    public Country getLooser() {
        if (!hasResultSet()) {
            return null;
        }

        if (isTeamOneWinner()) {
            return teamTwo.getCountry();
        }

        if (isTeamTwoWinner()) {
            return teamOne.getCountry();
        }

        return null;
    }

    public Integer getGoalsTeamOne() {
        return teamOne.getGoals();
    }

    public void setGoalsTeamOne(Integer goalsTeamOne) {
        this.teamOne.setGoals(goalsTeamOne);
    }

    public Integer getGoalsTeamTwo() {
        return teamTwo.getGoals();
    }

    public void setGoalsTeamTwo(Integer goalsTeamTwo) {
        this.teamTwo.setGoals(goalsTeamTwo);
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
        Match match = (Match) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(id, match.id);
        builder.append(teamOne, match.teamOne);
        builder.append(teamTwo, match.teamTwo);
        builder.append(group, match.group);
        builder.append(kickOffDate, match.kickOffDate);
        builder.append(stadium, match.stadium);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(id);
        builder.append(teamOne);
        builder.append(teamTwo);
        builder.append(group);
        builder.append(kickOffDate);
        builder.append(stadium);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("teamOne", teamOne);
        builder.append("teamTwo", teamTwo);
        builder.append("group", group);
        builder.append("kickOffDate", kickOffDate);
        builder.append("stadium", stadium);
        return builder.toString();
    }

    public Long getId() {
        return id;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public LocalDateTime getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(LocalDateTime kickOffDate) {
        this.kickOffDate = kickOffDate;
    }

    public boolean isBettable() {
        return !hasStarted() && !hasResultSet();
    }

    public boolean isFinal() {
        return isGroup(Group.FINAL);
    }

    public boolean isPenaltyWinnerOne() {
        return penaltyWinnerOne;
    }

    public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
        this.penaltyWinnerOne = penaltyWinnerOne;
    }

    public boolean isGroupMatch() {
        return this.group.name().startsWith("GROUP");
    }

    public String getCssClassPenaltyWinnerOne() {
        if (this.isGroupMatch() || !this.isUndecidedResult()) {
            return "";
        }
        return this.isPenaltyWinnerOne() ? FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS : "";
    }

    public String getCssClassPenaltyWinnerTwo() {
        if (this.isGroupMatch() || !this.isUndecidedResult()) {
            return "";
        }
        return !this.isPenaltyWinnerOne() ? FredbetConstants.BADGE_PENALTY_WINNER_MATCH_CSS_CLASS : "";
    }

    public boolean isGroup(Group group) {
        return this.group.equals(group);
    }

    @Override
    public Team getTeamOne() {
        if (this.teamOne == null) {
            this.teamOne = new Team();
        }
        return teamOne;
    }

    public void setTeamOne(Team teamOne) {
        this.teamOne = teamOne;
    }

    @Override
    public Team getTeamTwo() {
        if (this.teamTwo == null) {
            this.teamTwo = new Team();
        }
        return teamTwo;
    }

    public void setTeamTwo(Team teamTwo) {
        this.teamTwo = teamTwo;
    }

    public String getLabel(MessageSourceUtil messageSourceUtil, Locale locale) {
        return getTeamOne().getNameTranslated(messageSourceUtil, locale) + " - "
               + getTeamTwo().getNameTranslated(messageSourceUtil, locale);
    }

    @Override
    public String getBusinessHashcode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(this.teamOne.getBusinessKey());
        builder.append(this.teamTwo.getBusinessKey());
        builder.append(this.group);
        builder.append(dateTimeFormatter.format(this.kickOffDate));
        return "" + builder.toHashCode();
    }

    public String getKickOffDateFormated() {
        return this.kickOffDate == null ? null : dateTimeFormatter.format(this.kickOffDate);
    }
}
