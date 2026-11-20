package de.fred4jupiter.fredbet.csvimport;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The content of an uploaded CSV file: the header line and the data rows.
 *
 * @param headers column headers of the first line
 * @param rows    data rows (without the header line), each with as many values as there are headers
 */
public record CsvTable(List<String> headers, List<List<String>> rows) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public CsvTable {
        // copy into serializable lists, because the table is kept in the HTTP session
        headers = new ArrayList<>(headers);
        rows = new ArrayList<>(rows.stream().map(row -> (List<String>) new ArrayList<>(row)).toList());
    }

    public int columnCount() {
        return headers.size();
    }

    public int rowCount() {
        return rows.size();
    }

    /**
     * The first rows of the table, e.g. for a preview.
     */
    public List<List<String>> firstRows(int maxRows) {
        return rows.subList(0, Math.min(maxRows, rows.size()));
    }

    /**
     * Line number of the given row in the CSV file (the header is line 1).
     */
    public static int lineNumberOf(int rowIndex) {
        return rowIndex + 2;
    }
}
