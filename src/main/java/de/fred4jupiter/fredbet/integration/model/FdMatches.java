package de.fred4jupiter.fredbet.integration.model;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public record FdMatches(List<FdMatch> matches) {

    public boolean isCompetitionCompleted() {
        LocalDate seasonEndDate = matches.getFirst().season().endDate();
        return LocalDate.now().isAfter(seasonEndDate);
    }

    public boolean hasMatches() {
        return matches != null && !matches.isEmpty();
    }

    public FdMatches createNewWithoutResults() {
        return createNew(fdMatch -> fdMatch.createNewWithGoals(null, null));
    }

    public FdMatches createNewWithUpdatedTimestamp() {
        return createNew(fdMatch -> fdMatch.createNewWithGoals(fdMatch.score().fullTime().home(), fdMatch.score().fullTime().away()));
    }

    private FdMatches createNew(Function<FdMatch, FdMatch> mapper) {
        return new FdMatches(matches.stream().map(mapper).toList());
    }
}
