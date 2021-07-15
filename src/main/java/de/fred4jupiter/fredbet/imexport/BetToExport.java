package de.fred4jupiter.fredbet.imexport;

class BetToExport {

    private String username;

    private Long matchId;

    private Integer goalsTeamOne;

    private Integer goalsTeamTwo;

    private Integer points;

    private boolean penaltyWinnerOne;

    private boolean joker;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public boolean isPenaltyWinnerOne() {
        return penaltyWinnerOne;
    }

    public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
        this.penaltyWinnerOne = penaltyWinnerOne;
    }

    public boolean isJoker() {
        return joker;
    }

    public void setJoker(boolean joker) {
        this.joker = joker;
    }
}
