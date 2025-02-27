package de.fred4jupiter.fredbet.points;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PointsConfiguration {

    private ExtraPointsConfiguration extraPointsConfig;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectResult = 3;

    @NotNull
    @Min(value = 0)
    private Integer pointsSameGoalDifference = 2;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectWinner = 1;

    @NotNull
    @Min(value = 0)
    private Integer pointsCorrectNumberOfGoalsOneTeam = 0;

    public ExtraPointsConfiguration getExtraPointsConfig() {
        return extraPointsConfig;
    }

    public void setExtraPointsConfig(ExtraPointsConfiguration extraPointsConfig) {
        this.extraPointsConfig = extraPointsConfig;
    }

    public Integer getPointsCorrectResult() {
        return pointsCorrectResult;
    }

    public void setPointsCorrectResult(Integer pointsCorrectResult) {
        this.pointsCorrectResult = pointsCorrectResult;
    }

    public Integer getPointsSameGoalDifference() {
        return pointsSameGoalDifference;
    }

    public void setPointsSameGoalDifference(Integer pointsSameGoalDifference) {
        this.pointsSameGoalDifference = pointsSameGoalDifference;
    }

    public Integer getPointsCorrectWinner() {
        return pointsCorrectWinner;
    }

    public void setPointsCorrectWinner(Integer pointsCorrectWinner) {
        this.pointsCorrectWinner = pointsCorrectWinner;
    }

    public Integer getPointsCorrectNumberOfGoalsOneTeam() {
        return pointsCorrectNumberOfGoalsOneTeam;
    }

    public void setPointsCorrectNumberOfGoalsOneTeam(Integer pointsCorrectNumberOfGoalsOneTeam) {
        this.pointsCorrectNumberOfGoalsOneTeam = pointsCorrectNumberOfGoalsOneTeam;
    }
}
