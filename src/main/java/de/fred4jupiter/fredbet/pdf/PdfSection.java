package de.fred4jupiter.fredbet.pdf;

import java.util.List;

/**
 * A table with its own heading within a PDF, created by {@link PdfExportService#createSection}.
 */
public record PdfSection(PdfTableData tableData, List<PdfExportService.RowContentAdder> rows) {
}
