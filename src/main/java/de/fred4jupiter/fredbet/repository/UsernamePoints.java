package de.fred4jupiter.fredbet.repository;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UsernamePoints {

    private String userName;

    private Integer totalPoints = 0;

    private Integer goalDifference = 0;

    private Integer correctResultCount = 0;

    private boolean child;

    private String cssRankClass;

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public Integer getCorrectResultCount() {
        return correctResultCount;
    }

    public void setCorrectResultCount(Integer correctResultCount) {
        this.correctResultCount = correctResultCount;
    }

    public Integer getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(Integer goalDifference) {
        this.goalDifference = goalDifference;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("userName", userName);
        builder.append("totalPoints", totalPoints);
        builder.append("goalDifference", goalDifference);
        builder.append("correctResultCount", correctResultCount);
        return builder.toString();
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getCssRankClass() {
        return cssRankClass;
    }

    public void setCssRankClass(String cssRankClass) {
        this.cssRankClass = cssRankClass;
    }
}
