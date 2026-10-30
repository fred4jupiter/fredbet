package de.fred4jupiter.fredbet.pdf;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

/**
 * @param subtitle         optional line below the title (may be null)
 * @param columnAlignments horizontal alignment per column (see {@link com.lowagie.text.Element})
 * @param legendEntries    optional explanations of row colors, shown below the table
 */
public record PdfTableData(Locale locale, String title, String subtitle, List<String> headerColumns, List<Integer> columnAlignments,
                           float[] columnWidths, List<LegendEntry> legendEntries) {

    public record LegendEntry(Color color, String text) {
    }
}
