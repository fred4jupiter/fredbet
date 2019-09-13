package de.fred4jupiter.fredbet.service.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    public <T> byte[] createPdfFileFrom(String title, List<String> headerColumns, List<T> data, RowCallback<T> rowCallback) {
        try (Document document = new Document(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = new Font(Font.COURIER, 18.0f, Font.BOLD);
            Paragraph headline = new Paragraph(title, font);
            headline.setSpacingAfter(20);
            document.add(headline);

            Paragraph dateParagraph = new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
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
        for (String column : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(column));
            if (isHeader) {
                cell.setBackgroundColor(Color.LIGHT_GRAY);
            }

            table.addCell(cell);
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
