package de.fred4jupiter.fredbet.service.pdf;

import java.util.List;
import java.util.Locale;

class PdfTableData {

    private Locale locale;

    private String title;

    private List<String> headerColumns;

    private float[] columnWidths;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getHeaderColumns() {
        return headerColumns;
    }

    public void setHeaderColumns(List<String> headerColumns) {
        this.headerColumns = headerColumns;
    }

    public float[] getColumnWidths() {
        return columnWidths;
    }

    public void setColumnWidths(float[] columnWidths) {
        this.columnWidths = columnWidths;
    }
}
