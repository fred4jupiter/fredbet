package de.fred4jupiter.fredbet.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
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
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

@Service
public class PdfExportService {

    private static final Logger LOG = LoggerFactory.getLogger(PdfExportService.class);

    // DIN A4 with the bottom margin leaving room for the page footer
    private static final float MARGIN = 36;
    private static final float MARGIN_BOTTOM = 54;

    private static final float TABLE_FONT_SIZE = 10;
    private static final float SECTION_HEADING_FONT_SIZE = 11;
    private static final float FOOTER_FONT_SIZE = 8;

    private static final Color HEADER_BACKGROUND = new Color(52, 58, 64);
    private static final Color STRIPE_BACKGROUND = new Color(245, 246, 247);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SECONDARY_TEXT = new Color(108, 117, 125);

    // left indentation of the bullets/numbers of HTML lists and the minimal gap between bullet/number and text
    private static final float LIST_INDENTATION = 12;
    private static final float LIST_SYMBOL_GAP = 5;

    private final MessageSourceUtil messageSourceUtil;

    private final FontCreator fontCreator;

    PdfExportService(MessageSourceUtil messageSourceUtil, FontCreator fontCreator) {
        this.messageSourceUtil = messageSourceUtil;
        this.fontCreator = fontCreator;
    }

    public <T> byte[] createPdfFileFrom(PdfTableData pdfTableData, List<T> data, BiConsumer<RowContentAdder, T> rowCallback) {
        return createPdf(new PageFooter(pdfTableData.title(), pdfTableData.locale()), document -> {
            document.add(createHeadline(pdfTableData.title()));
            document.add(createSubtitleParagraph(pdfTableData.subtitle(), pdfTableData.locale()));
            document.add(createTable(pdfTableData, createRows(data, rowCallback), null));
            if (!pdfTableData.legendEntries().isEmpty()) {
                document.add(createLegend(pdfTableData.legendEntries()));
            }
        });
    }

    /**
     * Creates a section for {@link #createPdfWithSections}: a table with its own heading. The title of the given table
     * data is used as the heading and the subtitle is shown next to it. The legend entries of the table data are ignored.
     */
    public <T> PdfSection createSection(PdfTableData pdfTableData, List<T> data, BiConsumer<RowContentAdder, T> rowCallback) {
        return new PdfSection(pdfTableData, createRows(data, rowCallback));
    }

    /**
     * Creates a PDF with several tables, each with its own heading. The heading and the column headers are repeated when
     * a table continues on the next page, and small tables are not split over two pages.
     *
     * @param subtitle optional line below the title (may be null)
     * @param legend   explanations of the cell colors, shown at the end of the document
     */
    public byte[] createPdfWithSections(String title, String subtitle, Locale locale, List<PdfSection> sections,
                                        List<PdfTableData.LegendEntry> legend) {
        return createPdf(new PageFooter(title, locale), document -> {
            document.add(createHeadline(title));
            document.add(createSubtitleParagraph(subtitle, locale));
            for (PdfSection section : sections) {
                PdfPTable table = createTable(section.tableData(), section.rows(), createSectionHeadingCell(section.tableData()));
                table.setKeepTogether(true);
                document.add(table);
            }
            if (!legend.isEmpty()) {
                document.add(createLegend(legend));
            }
        });
    }

    private <T> List<RowContentAdder> createRows(List<T> data, BiConsumer<RowContentAdder, T> rowCallback) {
        return data.stream().map(entry -> {
            RowContentAdder rowContentAdder = new RowContentAdder();
            rowCallback.accept(rowContentAdder, entry);
            return rowContentAdder;
        }).toList();
    }

    private PdfPCell createSectionHeadingCell(PdfTableData pdfTableData) {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(pdfTableData.title(), createFont(SECTION_HEADING_FONT_SIZE, Font.BOLD, Color.BLACK)));
        if (StringUtils.isNotBlank(pdfTableData.subtitle())) {
            phrase.add(new Chunk("   " + pdfTableData.subtitle(), createFont(FOOTER_FONT_SIZE + 1, Font.NORMAL, SECONDARY_TEXT)));
        }

        PdfPCell cell = new PdfPCell(phrase);
        cell.setColspan(pdfTableData.headerColumns().size());
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(14);
        cell.setPaddingBottom(5);
        cell.setPaddingLeft(0);
        return cell;
    }

    /**
     * Creates a PDF from simple HTML (e.g. edited with the WYSIWYG editor) with the same headline and page footer as the
     * table exports.
     */
    public byte[] createPdfFromHtml(String title, String html, Locale locale) {
        return createPdf(new PageFooter(title, locale), document -> {
            document.add(createHeadline(title));
            document.add(createSubtitleParagraph(null, locale));

            List<Element> elements = HTMLWorker.parseToList(new StringReader(html), createHtmlStyleSheet(),
                Map.of("font_factory", fontCreator.createFontProvider()));
            for (Element element : elements) {
                if (element instanceof com.lowagie.text.List list) {
                    list.setIndentationLeft(list.getIndentationLeft() + LIST_INDENTATION);
                    alignListItems(list);
                }
                document.add(element);
            }
        });
    }

    /**
     * The HTML parser indents the list item text by the exact width of the bullet/number, so the text sticks to it and
     * the text of "9." and "10." is not aligned. This uses the same hanging indentation for all items of a list.
     */
    private void alignListItems(com.lowagie.text.List list) {
        float symbolWidth = 0;
        for (Element element : list.getItems()) {
            if (element instanceof ListItem item) {
                symbolWidth = Math.max(symbolWidth, item.getListSymbol().getWidthPoint());
            }
        }

        final float indentation = symbolWidth + LIST_SYMBOL_GAP;
        for (Element element : list.getItems()) {
            if (element instanceof ListItem item) {
                item.setIndentationLeft(indentation);
            } else if (element instanceof com.lowagie.text.List nested) {
                alignListItems(nested);
            }
        }
    }

    /**
     * Styles of the HTML tags, similar to the Bootstrap defaults of the web page. Sizes are in pt, "leading" is a
     * multiple of the font size and "before"/"after" are the spacings around paragraphs.
     */
    private StyleSheet createHtmlStyleSheet() {
        StyleSheet styles = new StyleSheet();
        styles.loadTagStyle("body", "size", "10.5pt");
        styles.loadTagStyle("body", "leading", "0,1.35");
        styles.loadTagStyle("body", "color", "#212529");
        styles.loadTagStyle("body", "after", "6");

        loadHeadingStyle(styles, "h1", "16pt", 14);
        loadHeadingStyle(styles, "h2", "13pt", 12);
        loadHeadingStyle(styles, "h3", "11.5pt", 10);
        loadHeadingStyle(styles, "h4", "10.5pt", 8);

        styles.loadTagStyle("li", "after", "3");
        return styles;
    }

    private void loadHeadingStyle(StyleSheet styles, String tag, String size, float spacingBefore) {
        styles.loadTagStyle(tag, "size", size);
        styles.loadTagStyle(tag, "b", "");
        styles.loadTagStyle(tag, "leading", "0,1.2");
        styles.loadTagStyle(tag, "before", String.valueOf(spacingBefore));
        styles.loadTagStyle(tag, "after", "4");
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

    /**
     * @param headingCell optional heading above the column headers (may be null), repeated together with them on every page
     */
    private PdfPTable createTable(PdfTableData pdfTableData, List<RowContentAdder> rows, PdfPCell headingCell) {
        PdfPTable table = new PdfPTable(pdfTableData.columnWidths());
        table.setWidthPercentage(100);
        if (headingCell != null) {
            table.addCell(headingCell);
        }
        // the header rows are repeated on every page
        table.setHeaderRows(headingCell != null ? 2 : 1);

        final Font headerFont = createFont(TABLE_FONT_SIZE, Font.BOLD, Color.WHITE);
        final List<String> headerColumns = pdfTableData.headerColumns();
        for (int i = 0; i < headerColumns.size(); i++) {
            PdfPCell cell = createCell(headerColumns.get(i), headerFont, pdfTableData.columnAlignments().get(i));
            cell.setBackgroundColor(HEADER_BACKGROUND);
            table.addCell(cell);
        }

        final Font font = createFont(TABLE_FONT_SIZE, Font.NORMAL, Color.BLACK);
        for (int row = 0; row < rows.size(); row++) {
            RowContentAdder rowContentAdder = rows.get(row);

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

    private Paragraph createLegend(List<PdfTableData.LegendEntry> legendEntries) {
        Paragraph legend = new Paragraph();
        legend.setSpacingBefore(8);
        final Font font = createFont(FOOTER_FONT_SIZE + 1, Font.NORMAL, SECONDARY_TEXT);
        for (PdfTableData.LegendEntry entry : legendEntries) {
            Chunk colorBox = new Chunk("    ", font);
            colorBox.setBackground(entry.color(), 0, 1, 0, 1);
            legend.add(colorBox);
            legend.add(new Chunk("  " + entry.text() + "    ", font));
        }
        return legend;
    }

    private Paragraph createSubtitleParagraph(String subtitleText, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withLocale(locale);
        String dateTime = ZonedDateTime.now().format(formatter);
        String text = StringUtils.isBlank(subtitleText) ? dateTime : subtitleText + " · " + dateTime;

        Paragraph subtitle = new Paragraph(text, createFont(TABLE_FONT_SIZE, Font.NORMAL, SECONDARY_TEXT));
        subtitle.setSpacingAfter(12);
        return subtitle;
    }

    private Paragraph createHeadline(String title) {
        Paragraph headline = new Paragraph(title, createFont(18, Font.BOLD, Color.BLACK));
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

        private final String title;

        private final Locale locale;

        private final Font font = createFont(FOOTER_FONT_SIZE, Font.NORMAL, SECONDARY_TEXT);

        private PdfTemplate totalPages;

        private PageFooter(String title, Locale locale) {
            this.title = title;
            this.locale = locale;
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

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, new Phrase(title, font), document.left(), textY, 0);

            String pageLabel = messageSourceUtil.getMessageFor("page", locale);
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
