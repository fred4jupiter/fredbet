package de.fred4jupiter.fredbet.pdf;

import com.lowagie.text.Element;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfTableDataBuilder {
    private Locale locale;
    private String title;
    private String subtitle;
    private final List<String> headerColumns = new ArrayList<>();
    private final List<Integer> columnAlignments = new ArrayList<>();
    private final List<PdfTableData.LegendEntry> legendEntries = new ArrayList<>();

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

    public PdfTableDataBuilder withSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public PdfTableDataBuilder withHeaderColumn(String headerColumn) {
        return withHeaderColumn(headerColumn, Element.ALIGN_CENTER);
    }

    /**
     * @param alignment horizontal alignment of the column, e.g. {@link Element#ALIGN_LEFT}
     */
    public PdfTableDataBuilder withHeaderColumn(String headerColumn, int alignment) {
        this.headerColumns.add(headerColumn);
        this.columnAlignments.add(alignment);
        return this;
    }

    public PdfTableDataBuilder withColumnWidths(float[] columnWidths) {
        this.columnWidths = columnWidths;
        return this;
    }

    public PdfTableDataBuilder withLegendEntry(Color color, String text) {
        this.legendEntries.add(new PdfTableData.LegendEntry(color, text));
        return this;
    }

    public PdfTableData build() {
        return new PdfTableData(locale, title, subtitle, List.copyOf(headerColumns), List.copyOf(columnAlignments), columnWidths,
            List.copyOf(legendEntries));
    }
}
