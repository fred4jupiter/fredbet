package de.fred4jupiter.fredbet.service.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
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
import java.util.function.BiConsumer;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    private final MessageSourceUtil messageSourceUtil;

    private final FontCreator fontCreator;

    public PdfExportService(MessageSourceUtil messageSourceUtil, FontCreator fontCreator) {
        this.messageSourceUtil = messageSourceUtil;
        this.fontCreator = fontCreator;
    }

    public <T> byte[] createPdfFileFrom(PdfTableDataBuilder pdfTableDataBuilder, List<T> data, BiConsumer<RowContentAdder, T> rowCallback) {
        final PdfTableData pdfTableData = pdfTableDataBuilder.build();

        try (Document document = new Document(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.setFooter(createFooter(pdfTableData));

            document.open();

            document.add(createHeadline(pdfTableData));
            document.add(createCurrenteDateTimeParagraph(pdfTableData));
            document.add(createTable(data, rowCallback, pdfTableData));

            document.close();
            out.flush();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private <T> PdfPTable createTable(List<T> data, BiConsumer<RowContentAdder, T> rowCallback, PdfTableData pdfTableData) {
        PdfPTable table = new PdfPTable(pdfTableData.getColumnWidths());
        table.setWidthPercentage(100);

        addRowToTable(table, pdfTableData.getHeaderColumns(), true);

        data.forEach(dataEntry -> {
            RowContentAdder rowContentAdder = new RowContentAdder();
            rowCallback.accept(rowContentAdder, dataEntry);
            List<String> rowContent = rowContentAdder.getRowContent();
            addRowToTable(table, rowContent, false);
        });
        return table;
    }

    private HeaderFooter createFooter(PdfTableData pdfTableData) {
        String pageLabel = messageSourceUtil.getMessageFor("page", pdfTableData.getLocale());
        HeaderFooter footer = new HeaderFooter(new Phrase(pageLabel + ": ", fontCreator.createFont()), true);
        footer.setBorder(Rectangle.NO_BORDER);
        footer.setAlignment(Element.ALIGN_RIGHT);
        return footer;
    }

    private Paragraph createCurrenteDateTimeParagraph(PdfTableData pdfTableData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(pdfTableData.getLocale());
        Paragraph dateParagraph = new Paragraph(ZonedDateTime.now().format(formatter), fontCreator.createFont());
        dateParagraph.setSpacingAfter(10);
        return dateParagraph;
    }

    private Paragraph createHeadline(PdfTableData pdfTableData) {
        Font font = fontCreator.createFont();
        font.setSize(18);
        font.setStyle(Font.BOLD);
        Paragraph headline = new Paragraph(pdfTableData.getTitle(), font);
        headline.setSpacingAfter(20);
        return headline;
    }

    private void addRowToTable(PdfPTable table, List<String> columns, boolean isHeader) {
        final Font font = fontCreator.createFont();

        for (String column : columns) {
            PdfPCell cell = new PdfPCell(new Phrase(column, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (isHeader) {
                cell.setBackgroundColor(Color.LIGHT_GRAY);
            }

            table.addCell(cell);
        }
    }

    public static final class RowContentAdder {
        private final List<String> rowContent = new ArrayList<>();

        public void addCellContent(String content) {
            this.rowContent.add(content);
        }

        public List<String> getRowContent() {
            return rowContent;
        }
    }
}
