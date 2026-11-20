package de.fred4jupiter.fredbet.csvimport;

/**
 * A problem found while importing a CSV file.
 *
 * @param lineNumber line number in the CSV file or null, if the problem does not belong to a line (e.g. a mapping problem)
 * @param msgKey     key of the translated message
 * @param field      the affected field or null
 * @param value      the affected value of the CSV file or null
 */
public record CsvImportError(Integer lineNumber, String msgKey, CsvImportField field, String value) {

    static CsvImportError ofMapping(String msgKey, CsvImportField field) {
        return new CsvImportError(null, msgKey, field, null);
    }

    static CsvImportError ofLine(int lineNumber, String msgKey, CsvImportField field, String value) {
        return new CsvImportError(lineNumber, msgKey, field, value);
    }
}
