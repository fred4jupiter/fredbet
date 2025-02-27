package de.fred4jupiter.fredbet.points;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ExtraPointsConfiguration {

    /**
     * Extra betting points for final winner.
     */
    @NotNull
    @Min(value = 0)
    private Integer pointsFinalWinner = 10;

    /**
     * Extra betting points for semi final winner.
     */
    @NotNull
    @Min(value = 0)
    private Integer pointsSemiFinalWinner = 5;

    /**
     * Extra betting points for third winner.
     */
    @NotNull
    @Min(value = 0)
    private Integer pointsThirdFinalWinner = 2;

    public @NotNull @Min(value = 0) Integer getPointsFinalWinner() {
        return pointsFinalWinner;
    }

    public void setPointsFinalWinner(@NotNull @Min(value = 0) Integer pointsFinalWinner) {
        this.pointsFinalWinner = pointsFinalWinner;
    }

    public @NotNull @Min(value = 0) Integer getPointsSemiFinalWinner() {
        return pointsSemiFinalWinner;
    }

    public void setPointsSemiFinalWinner(@NotNull @Min(value = 0) Integer pointsSemiFinalWinner) {
        this.pointsSemiFinalWinner = pointsSemiFinalWinner;
    }

    public @NotNull @Min(value = 0) Integer getPointsThirdFinalWinner() {
        return pointsThirdFinalWinner;
    }

    public void setPointsThirdFinalWinner(@NotNull @Min(value = 0) Integer pointsThirdFinalWinner) {
        this.pointsThirdFinalWinner = pointsThirdFinalWinner;
    }
}
