package de.fred4jupiter.fredbet.repository;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UsernamePoints {

    private final String userName;

    // 1. Criteria
    private final Integer totalPoints;

    // 2. Criteria
    private Integer correctResultCount = 0;

    // 3. Criteria
    private Integer goalDifference = 0;

    private boolean child;

    private String cssRankClass;

    private boolean sameRankingPositionAsOtherUser;

    private boolean topTipperOfToday;

    public UsernamePoints(String userName, Integer totalPoints) {
        this.userName = userName;
        this.totalPoints = totalPoints;
    }

    public boolean isSameRankingPositionAsOtherUser() {
        return sameRankingPositionAsOtherUser;
    }

    public void setSameRankingPositionAsOtherUser(boolean sameRankingPositionAsOtherUser) {
        this.sameRankingPositionAsOtherUser = sameRankingPositionAsOtherUser;
    }

    public int getUniqueHash() {
        return new HashCodeBuilder().append(totalPoints).append(correctResultCount).append(goalDifference).toHashCode();
    }

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

    public String getCssRankClass() {
        return cssRankClass;
    }

    public void setCssRankClass(String cssRankClass) {
        this.cssRankClass = cssRankClass;
    }

    public boolean isTopTipperOfToday() {
        return topTipperOfToday;
    }

    public void setTopTipperOfToday(boolean topTipperOfToday) {
        this.topTipperOfToday = topTipperOfToday;
    }
}
