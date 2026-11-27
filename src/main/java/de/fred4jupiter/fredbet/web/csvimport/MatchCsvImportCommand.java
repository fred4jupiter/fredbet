package de.fred4jupiter.fredbet.web.csvimport;

import de.fred4jupiter.fredbet.csvimport.CsvImportField;
import de.fred4jupiter.fredbet.csvimport.CsvTable;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the uploaded CSV file and the column mapping in the HTTP session between the upload and the import step.
 */
public class MatchCsvImportCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fileName;

    private CsvTable csvTable;

    // the mapped field of each column (in the order of the columns), null for a column that is not imported
    private List<CsvImportField> columnMappings = new ArrayList<>();

    private String dateFormat;

    public boolean isUploaded() {
        return csvTable != null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CsvTable getCsvTable() {
        return csvTable;
    }

    public void setCsvTable(CsvTable csvTable) {
        this.csvTable = csvTable;
    }

    public List<CsvImportField> getColumnMappings() {
        return columnMappings;
    }

    public void setColumnMappings(List<CsvImportField> columnMappings) {
        this.columnMappings = columnMappings != null ? new ArrayList<>(columnMappings) : new ArrayList<>();
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
