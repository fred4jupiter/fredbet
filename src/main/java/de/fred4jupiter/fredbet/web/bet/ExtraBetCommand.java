package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;

import jakarta.persistence.Column;

public class ExtraBetCommand {

    private Long extraBetId;

    private Country finalWinner;
    private Country semiFinalWinner;
    private Country thirdFinalWinner;

    private Integer pointsOne = 0;

    private Integer pointsTwo = 0;

    private Integer pointsThree = 0;

    private Match finalMatch;

    public Country getFinalWinner() {
        return finalWinner;
    }

    public void setFinalWinner(Country finalWinner) {
        this.finalWinner = finalWinner;
    }

    public Country getSemiFinalWinner() {
        return semiFinalWinner;
    }

    public void setSemiFinalWinner(Country semiFinalWinner) {
        this.semiFinalWinner = semiFinalWinner;
    }

    public Long getExtraBetId() {
        return extraBetId;
    }

    public void setExtraBetId(Long extraBetId) {
        this.extraBetId = extraBetId;
    }

    public Integer getPoints() {
        if (finalMatch == null) {
            return 0;
        }

        if (finalMatch.hasResultSet() && (pointsOne == null || pointsTwo == null || pointsThree == null)) {
            return 0;
        }
        return pointsOne + pointsTwo + pointsThree;
    }

    public Match getFinalMatch() {
        return finalMatch;
    }

    public void setFinalMatch(Match finalMatch) {
        this.finalMatch = finalMatch;
    }

    public Country getThirdFinalWinner() {
        return thirdFinalWinner;
    }

    public void setThirdFinalWinner(Country thirdFinalWinner) {
        this.thirdFinalWinner = thirdFinalWinner;
    }

    public void setPointsOne(Integer pointsOne) {
        this.pointsOne = pointsOne;
    }

    public void setPointsTwo(Integer pointsTwo) {
        this.pointsTwo = pointsTwo;
    }

    public void setPointsThree(Integer pointsThree) {
        this.pointsThree = pointsThree;
    }

    public Integer getPointsOne() {
        return pointsOne;
    }

    public Integer getPointsTwo() {
        return pointsTwo;
    }

    public Integer getPointsThree() {
        return pointsThree;
    }
}
