package de.fred4jupiter.fredbet.points;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;

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

    public static PointsConfiguration withDefaults() {
        PointsConfiguration pointsConfig = new PointsConfiguration();
        pointsConfig.setPointsCorrectResult(3);
        pointsConfig.setPointsSameGoalDifference(2);
        pointsConfig.setPointsCorrectWinner(1);
        pointsConfig.setPointsCorrectNumberOfGoalsOneTeam(0);
        return pointsConfig;
    }

    public ExtraPointsConfiguration getExtraPointsConfig() {
        if (this.extraPointsConfig == null) {
            this.extraPointsConfig = createWithDefaults();
        }
        return extraPointsConfig;
    }

    private @NonNull ExtraPointsConfiguration createWithDefaults() {
        ExtraPointsConfiguration extraPointsConfig = new ExtraPointsConfiguration();
        extraPointsConfig.setPointsFinalWinner(10);
        extraPointsConfig.setPointsSemiFinalWinner(5);
        extraPointsConfig.setPointsThirdFinalWinner(2);
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
