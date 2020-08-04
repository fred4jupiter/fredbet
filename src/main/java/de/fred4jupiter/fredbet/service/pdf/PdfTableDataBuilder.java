package de.fred4jupiter.fredbet.service.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfTableDataBuilder {
    private Locale locale;
    private String title;
    private final List<String> headerColumns = new ArrayList<>();

    private float[] columnWidths;

    private PdfTableDataBuilder() {
        // use create method
    }

    public static PdfTableDataBuilder create() {
        return new PdfTableDataBuilder();
    }

    public PdfTableDataBuilder withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public PdfTableDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PdfTableDataBuilder withHeaderColumn(String headerColumn) {
        this.headerColumns.add(headerColumn);
        return this;
    }

    public PdfTableDataBuilder withColumnWidths(float[] columnWidths) {
        this.columnWidths = columnWidths;
        return this;
    }

    public PdfTableData build() {
        PdfTableData pdfTableData = new PdfTableData();
        pdfTableData.setLocale(locale);
        pdfTableData.setTitle(title);
        pdfTableData.setHeaderColumns(headerColumns);
        pdfTableData.setColumnWidths(columnWidths);
        return pdfTableData;
    }
}
