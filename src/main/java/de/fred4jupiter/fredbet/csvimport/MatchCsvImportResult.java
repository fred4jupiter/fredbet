package de.fred4jupiter.fredbet.csvimport;

import java.util.List;

/**
 * Result of a CSV match import. If there are errors, nothing has been imported.
 *
 * @param importedMatches number of imported matches
 * @param createdTeams    number of newly created teams
 * @param errors          problems that prevented the import
 * @param warnings        problems that did not prevent the import, e.g. a crest that could not be downloaded
 */
public record MatchCsvImportResult(int importedMatches, int createdTeams, List<CsvImportError> errors, List<CsvImportError> warnings) {

    static MatchCsvImportResult failed(List<CsvImportError> errors) {
        return new MatchCsvImportResult(0, 0, List.copyOf(errors), List.of());
    }

    public boolean isSuccessful() {
        return errors.isEmpty();
    }
}
