package de.fred4jupiter.fredbet.pdf;

import java.util.List;
import java.util.Locale;

record PdfTableData(Locale locale, String title, List<String> headerColumns, float[] columnWidths) {

}
