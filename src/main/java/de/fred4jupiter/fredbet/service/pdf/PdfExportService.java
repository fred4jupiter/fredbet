package de.fred4jupiter.fredbet.service.pdf;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    public <T> byte[] createPdfFileFrom(String[] headerColumns, List<T> data, RowCallback<T> rowCallback) {
        try (Document document = new Document(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();
            PdfPTable table = new PdfPTable(headerColumns.length);
            table.setWidthPercentage(100);
            addRowToTable(table, Arrays.asList(headerColumns), true);

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
