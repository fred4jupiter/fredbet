package de.fred4jupiter.fredbet.service.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    public <T> byte[] createPdfFileFrom(String title, List<String> headerColumns, List<T> data, RowCallback<T> rowCallback) {
        return createPdfFileFrom(title, headerColumns, data, rowCallback, Locale.getDefault());
    }

    public <T> byte[] createPdfFileFrom(String title, List<String> headerColumns, List<T> data, RowCallback<T> rowCallback, Locale locale) {
        try (Document document = new Document(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font font = new Font(Font.COURIER, 18.0f, Font.BOLD);
            Font font = createFont();
            font.setSize(18);
            font.setStyle(Font.BOLD);
            Paragraph headline = new Paragraph(title, font);
            headline.setSpacingAfter(20);
            document.add(headline);

            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale);
            Paragraph dateParagraph = new Paragraph(ZonedDateTime.now().format(formatter));
            dateParagraph.setSpacingAfter(10);
            document.add(dateParagraph);

            PdfPTable table = new PdfPTable(headerColumns.size());
            table.setWidthPercentage(100);

            addRowToTable(table, headerColumns, true);

            data.forEach(dataEntry -> {
                RowContentAdder rowContentAdder = new RowContentAdder();
                rowCallback.onRow(rowContentAdder, dataEntry);
                List<String> rowContent = rowContentAdder.getRowContent();
                addRowToTable(table, rowContent, false);
            });
            document.add(table);
            document.close();
            out.flush();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private void addRowToTable(PdfPTable table, List<String> columns, boolean isHeader) {
        final Font font = createFont();

        for (String column : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(column, font));
            if (isHeader) {
                cell.setBackgroundColor(Color.LIGHT_GRAY);
            }

            table.addCell(cell);
        }
    }

    private Font createFont() {
        try {
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, "UTF-8", BaseFont.NOT_EMBEDDED);
            return new Font(baseFont, 12);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @FunctionalInterface
    public interface RowCallback<T> {

        void onRow(RowContentAdder rowContentAdder, T row);
    }

    public static class RowContentAdder {
        private final List<String> rowContent = new ArrayList<>();

        public void addCellContent(String content) {
            this.rowContent.add(content);
        }

        public List<String> getRowContent() {
            return rowContent;
        }
    }
}
