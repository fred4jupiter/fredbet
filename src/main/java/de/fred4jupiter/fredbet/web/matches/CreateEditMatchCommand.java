package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.web.validation.ValidMatchConstraint;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ValidMatchConstraint
public class CreateEditMatchCommand {

    private Long matchId;

    private String teamOneUniqueId;
    private String teamOneName;

    private String teamTwoUniqueId;
    private String teamTwoName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    private LocalDateTime kickOffDate;

    private Group group;

    private String stadium;

    private boolean deletable;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public LocalDateTime getKickOffDate() {
        return kickOffDate;
    }

    public void setKickOffDate(LocalDateTime kickOffDate) {
        this.kickOffDate = kickOffDate;
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

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public String getTeamOneUniqueId() {
        return teamOneUniqueId;
    }

    public void setTeamOneUniqueId(String teamOneUniqueId) {
        this.teamOneUniqueId = teamOneUniqueId;
    }

    public String getTeamTwoUniqueId() {
        return teamTwoUniqueId;
    }

    public void setTeamTwoUniqueId(String teamTwoUniqueId) {
        this.teamTwoUniqueId = teamTwoUniqueId;
    }

    public String getTeamOneName() {
        return teamOneName;
    }

    public void setTeamOneName(String teamOneName) {
        this.teamOneName = teamOneName;
    }

    public String getTeamTwoName() {
        return teamTwoName;
    }

    public void setTeamTwoName(String teamTwoName) {
        this.teamTwoName = teamTwoName;
    }
}
