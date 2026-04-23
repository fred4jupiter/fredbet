package de.fred4jupiter.fredbet.domain.entity;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.imexport.MatchBusinessKey;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Table(name = "MATCHES")
public class Match implements MatchBusinessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATCH_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_one_id")
    private Team teamOne;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_two_id")
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

    @Column(name = "GOALS_TEAM_ONE")
    private Integer goalsTeamOne;

    @Column(name = "GOALS_TEAM_TWO")
    private Integer goalsTeamTwo;

    @Column(name = "EXTERNAL_LAST_UPDATED")
    private ZonedDateTime externalLastUpdated;

    @Column(name = "EXTERNAL_ID")
    private String externalId;

    @Transient
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public boolean hasMatchStarted() {
        return LocalDateTime.now().isAfter(getKickOffDate());
    }

    public boolean hasMatchFinished() {
        return goalsTeamOne != null && goalsTeamTwo != null;
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

    public boolean hasResultSet() {
        return this.goalsTeamOne != null && this.goalsTeamTwo != null;
    }

    public Integer getGoalDifference() {
        if (this.goalsTeamOne == null || this.goalsTeamTwo == null) {
            throw new IllegalStateException("Match has not finished! No goal results set!");
        }
        return Math.abs(this.goalsTeamOne - this.goalsTeamTwo);
    }

    /**
     * Goal difference is 0, e.g. 1:1 or 3:3
     *
     * @return true if undecided
     */
    public boolean isUndecidedResult() {
        if (this.goalsTeamOne == null || this.goalsTeamTwo == null) {
            return false;
        }
        return getGoalDifference() == 0;
    }

    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(kickOffDate);
    }

    public boolean isTeamOneWinner() {
        if (this.getGoalsTeamOne() == null || this.getGoalsTeamTwo() == null) {
            throw new IllegalStateException("Match has not finished! No goal results set!");
        }

        if (getGoalDifference() == 0) {
            if (isGroupMatch()) {
                return false;
            } else {
                // check penalty winner
                return penaltyWinnerOne;
            }
        }

        return this.getGoalsTeamOne() > this.getGoalsTeamTwo();
    }

    public boolean isTeamTwoWinner() {
        if (this.getGoalsTeamOne() == null || this.getGoalsTeamTwo() == null) {
            throw new IllegalStateException("Match has not finished! No goal results set!");
        }

        if (getGoalDifference() == 0) {
            if (isGroupMatch()) {
                return false;
            } else {
                // check penalty winner
                return !penaltyWinnerOne;
            }
        }

        return this.getGoalsTeamOne() < this.getGoalsTeamTwo();
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

    public Team getTeamOne() {
        if (this.teamOne == null) {
            this.teamOne = new Team();
        }
        return teamOne;
    }

    public void setTeamOne(Team teamOne) {
        this.teamOne = teamOne;
    }

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
    public String getBusinessKey() {
        return StringUtils.joinWith("_", this.teamOne.getBusinessKey(),
            this.teamTwo.getBusinessKey(), this.group, dateTimeFormatter.format(this.kickOffDate));
    }

    public String getKickOffDateFormated() {
        return this.kickOffDate == null ? null : dateTimeFormatter.format(this.kickOffDate);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ZonedDateTime getExternalLastUpdated() {
        return externalLastUpdated;
    }

    public void setExternalLastUpdated(ZonedDateTime externalLastUpdated) {
        this.externalLastUpdated = externalLastUpdated;
    }
}
