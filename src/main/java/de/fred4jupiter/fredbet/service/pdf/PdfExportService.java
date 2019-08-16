package de.fred4jupiter.fredbet.service.pdf;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Table;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    public <T> byte[] createPdfFileFrom(String[] headerColumns, List<T> data, RowCallback<T> rowCallback) {
        try (Document document = new Document(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();
            Table table = new Table(headerColumns.length, data.size());
            for (String headerColumn : headerColumns) {
                Cell cell = new Cell(headerColumn);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
                cell.setVerticalAlignment(VerticalAlignment.CENTER);
                table.addCell(cell);
            }

            data.forEach(dataEntry -> {
                RowContentAdder rowContentAdder = new RowContentAdder();
                rowCallback.onRow(rowContentAdder, dataEntry);
                List<String> rowContent = rowContentAdder.getRowContent();
                rowContent.forEach(cellContent -> {
                    Cell cell = new Cell(cellContent);
                    cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    cell.setVerticalAlignment(VerticalAlignment.CENTER);
                    table.addCell(cell);
                });
            });
            document.add(table);
            table.setConvert2pdfptable(true);
            document.close();
            out.flush();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    @FunctionalInterface
    public interface RowCallback<T> {

        void onRow(RowContentAdder rowContentAdder, T row);
    }

    public class RowContentAdder {
        private final List<String> rowContent = new ArrayList<>();

        public void addCellContent(String content) {
            this.rowContent.add(content);
        }

        public List<String> getRowContent() {
            return rowContent;
        }
    }
}
