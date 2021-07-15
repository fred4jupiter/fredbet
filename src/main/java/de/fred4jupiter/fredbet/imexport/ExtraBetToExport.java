package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.Country;

class ExtraBetToExport {

    private String userName;

    private Country finalWinner;

    private Country semiFinalWinner;

    private Country thirdFinalWinner;

    private Integer pointsOne;

    private Integer pointsTwo;

    private Integer pointsThree;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public Country getThirdFinalWinner() {
        return thirdFinalWinner;
    }

    public void setThirdFinalWinner(Country thirdFinalWinner) {
        this.thirdFinalWinner = thirdFinalWinner;
    }

    public Integer getPointsOne() {
        return pointsOne;
    }

    public void setPointsOne(Integer pointsOne) {
        this.pointsOne = pointsOne;
    }

    public Integer getPointsTwo() {
        return pointsTwo;
    }

    public void setPointsTwo(Integer pointsTwo) {
        this.pointsTwo = pointsTwo;
    }

    public Integer getPointsThree() {
        return pointsThree;
    }

    public void setPointsThree(Integer pointsThree) {
        this.pointsThree = pointsThree;
    }
}
