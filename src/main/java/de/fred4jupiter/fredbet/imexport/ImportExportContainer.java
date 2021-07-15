package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.service.user.UserToExport;

import java.util.List;

class ImportExportContainer {

    private List<UserToExport> users;

    private List<MatchToExport> matches;

    private List<BetToExport> bets;

    private List<ExtraBetToExport> extraBets;

    public List<UserToExport> getUsers() {
        return users;
    }

    public void setUsers(List<UserToExport> users) {
        this.users = users;
    }

    public List<MatchToExport> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchToExport> matches) {
        this.matches = matches;
    }

    public List<BetToExport> getBets() {
        return bets;
    }

    public void setBets(List<BetToExport> bets) {
        this.bets = bets;
    }

    public List<ExtraBetToExport> getExtraBets() {
        return extraBets;
    }

    public void setExtraBets(List<ExtraBetToExport> extraBets) {
        this.extraBets = extraBets;
    }
}
