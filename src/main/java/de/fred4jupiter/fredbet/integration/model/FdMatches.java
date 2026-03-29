package de.fred4jupiter.fredbet.integration.model;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public record FdMatches(List<FdMatch> matches) {

    public boolean isCompetitionCompleted() {
        LocalDate seasonEndDate = matches.getFirst().season().endDate();
        return LocalDate.now().isAfter(seasonEndDate);
    }

    public FdMatches createNewWithoutResults() {
        return createNewWithUpdatedTimestamp(fdMatch -> fdMatch.createNewWithGoals(null, null));
    }

    private FdMatches createNewWithUpdatedTimestamp(Function<FdMatch, FdMatch> mapper) {
        return new FdMatches(matches.stream().map(mapper).toList());
    }
}
