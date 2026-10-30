package de.fred4jupiter.fredbet.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.*;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    // DIN A4 with the bottom margin leaving room for the page footer
    private static final float MARGIN = 36;
    private static final float MARGIN_BOTTOM = 54;

    private static final float TABLE_FONT_SIZE = 10;
    private static final float FOOTER_FONT_SIZE = 8;

    private static final Color HEADER_BACKGROUND = new Color(52, 58, 64);
    private static final Color STRIPE_BACKGROUND = new Color(245, 246, 247);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SECONDARY_TEXT = new Color(108, 117, 125);

    private final MessageSourceUtil messageSourceUtil;

    private final FontCreator fontCreator;

    PdfExportService(MessageSourceUtil messageSourceUtil, FontCreator fontCreator) {
        this.messageSourceUtil = messageSourceUtil;
        this.fontCreator = fontCreator;
    }

    public <T> byte[] createPdfFileFrom(PdfTableData pdfTableData, List<T> data, BiConsumer<RowContentAdder, T> rowCallback) {
        return createPdf(new PageFooter(pdfTableData), document -> {
            document.add(createHeadline(pdfTableData));
            document.add(createSubtitleParagraph(pdfTableData));
            document.add(createTable(data, rowCallback, pdfTableData));
            if (!pdfTableData.legendEntries().isEmpty()) {
                document.add(createLegend(pdfTableData));
            }
        });
    }

    public byte[] createPdfFromHtml(String html) {
        return createPdf(null, document -> {
            HTMLWorker htmlWorker = new HTMLWorker(document);
            htmlWorker.parse(new StringReader(html));
        });
    }

    private byte[] createPdf(PdfPageEvent pageEvent, DocumentCallback documentCallback) {
        try (Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN_BOTTOM);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            if (pageEvent != null) {
                writer.setPageEvent(pageEvent);
            }
            document.open();
            documentCallback.onDocument(document);
            document.close();
            out.flush();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private <T> PdfPTable createTable(List<T> data, BiConsumer<RowContentAdder, T> rowCallback, PdfTableData pdfTableData) {
        PdfPTable table = new PdfPTable(pdfTableData.columnWidths());
        table.setWidthPercentage(100);
        // the header row is repeated on every page
        table.setHeaderRows(1);

        final Font headerFont = createFont(TABLE_FONT_SIZE, Font.BOLD, Color.WHITE);
        final List<String> headerColumns = pdfTableData.headerColumns();
        for (int i = 0; i < headerColumns.size(); i++) {
            PdfPCell cell = createCell(headerColumns.get(i), headerFont, pdfTableData.columnAlignments().get(i));
            cell.setBackgroundColor(HEADER_BACKGROUND);
            table.addCell(cell);
        }

        final Font font = createFont(TABLE_FONT_SIZE, Font.NORMAL, Color.BLACK);
        for (int row = 0; row < data.size(); row++) {
            RowContentAdder rowContentAdder = new RowContentAdder();
            rowCallback.accept(rowContentAdder, data.get(row));

            Color rowBackground = rowContentAdder.getRowBackground() != null ? rowContentAdder.getRowBackground()
                : (row % 2 == 1 ? STRIPE_BACKGROUND : null);

            List<CellContent> cells = rowContentAdder.getCells();
            for (int column = 0; column < cells.size(); column++) {
                CellContent cellContent = cells.get(column);
                PdfPCell cell = createCell(cellContent.content(), font, pdfTableData.columnAlignments().get(column));
                Color background = cellContent.background() != null ? cellContent.background() : rowBackground;
                if (background != null) {
                    cell.setBackgroundColor(background);
                }
                table.addCell(cell);
            }
        }
        return table;
    }

    private PdfPCell createCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(6);
        cell.setPaddingLeft(6);
        cell.setPaddingRight(6);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BORDER_COLOR);
        return cell;
    }

    private Paragraph createLegend(PdfTableData pdfTableData) {
        Paragraph legend = new Paragraph();
        legend.setSpacingBefore(8);
        final Font font = createFont(FOOTER_FONT_SIZE + 1, Font.NORMAL, SECONDARY_TEXT);
        for (PdfTableData.LegendEntry entry : pdfTableData.legendEntries()) {
            Chunk colorBox = new Chunk("    ", font);
            colorBox.setBackground(entry.color(), 0, 1, 0, 1);
            legend.add(colorBox);
            legend.add(new Chunk("  " + entry.text() + "    ", font));
        }
        return legend;
    }

    private Paragraph createSubtitleParagraph(PdfTableData pdfTableData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withLocale(pdfTableData.locale());
        String dateTime = ZonedDateTime.now().format(formatter);
        String text = StringUtils.isBlank(pdfTableData.subtitle()) ? dateTime : pdfTableData.subtitle() + " · " + dateTime;

        Paragraph subtitle = new Paragraph(text, createFont(TABLE_FONT_SIZE, Font.NORMAL, SECONDARY_TEXT));
        subtitle.setSpacingAfter(12);
        return subtitle;
    }

    private Paragraph createHeadline(PdfTableData pdfTableData) {
        Paragraph headline = new Paragraph(pdfTableData.title(), createFont(18, Font.BOLD, Color.BLACK));
        headline.setSpacingAfter(4);
        return headline;
    }

    private Font createFont(float size, int style, Color color) {
        Font font = fontCreator.createFont();
        font.setSize(size);
        font.setStyle(style);
        font.setColor(color);
        return font;
    }

    /**
     * Writes the title and "page n / total" at the bottom of every page. The total is only known when the document is
     * closed, so it is written into a placeholder template at the end.
     */
    private final class PageFooter extends PdfPageEventHelper {

        private static final float TOTAL_WIDTH = 24;

        private final PdfTableData pdfTableData;

        private final Font font = createFont(FOOTER_FONT_SIZE, Font.NORMAL, SECONDARY_TEXT);

        private PdfTemplate totalPages;

        private PageFooter(PdfTableData pdfTableData) {
            this.pdfTableData = pdfTableData;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPages = writer.getDirectContent().createTemplate(TOTAL_WIDTH, FOOTER_FONT_SIZE + 4);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContent();
            float lineY = document.bottom() - 14;
            float textY = lineY - 12;

            canvas.saveState();
            canvas.setColorStroke(BORDER_COLOR);
            canvas.setLineWidth(0.5f);
            canvas.moveTo(document.left(), lineY);
            canvas.lineTo(document.right(), lineY);
            canvas.stroke();
            canvas.restoreState();

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(pdfTableData.title(), font), document.left(), textY, 0);

            String pageLabel = messageSourceUtil.getMessageFor("page", pdfTableData.locale());
            String pageText = pageLabel + " " + writer.getPageNumber() + " /";
            float totalX = document.right() - TOTAL_WIDTH;
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, new Phrase(pageText, font), totalX, textY, 0);
            // trailing whitespace is trimmed when the text is right aligned, so the gap is added to the placeholder position
            float gap = font.getBaseFont().getWidthPoint(" ", FOOTER_FONT_SIZE);
            canvas.addTemplate(totalPages, totalX + gap, textY - 3);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            // the page number has already been increased for the next (not existing) page
            ColumnText.showTextAligned(totalPages, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1), font), 0, 3, 0);
        }
    }

    public static final class RowContentAdder {
        private final List<CellContent> cells = new ArrayList<>();

        private Color rowBackground;

        public void addCellContent(String content) {
            addCellContent(content, null);
        }

        /**
         * @param background background color of this cell only (overrides the row background), may be null
         */
        public void addCellContent(String content, Color background) {
            this.cells.add(new CellContent(content, background));
        }

        /**
         * Highlights the whole row instead of the default alternating background.
         */
        public void setRowBackground(Color rowBackground) {
            this.rowBackground = rowBackground;
        }

        public List<String> getRowContent() {
            return cells.stream().map(CellContent::content).toList();
        }

        List<CellContent> getCells() {
            return cells;
        }

        Color getRowBackground() {
            return rowBackground;
        }
    }

    private record CellContent(String content, Color background) {
    }

    @FunctionalInterface
    private interface DocumentCallback {

        void onDocument(Document document) throws IOException;
    }
}
