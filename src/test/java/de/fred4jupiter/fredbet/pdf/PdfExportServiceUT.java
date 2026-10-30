package de.fred4jupiter.fredbet.pdf;

import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.ranking.UsernamePoints;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import java.awt.Color;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@UnitTest
public class PdfExportServiceUT {

    @InjectMocks
    private PdfExportService pdfExportService;

    @Spy
    private FontCreator fontCreator = new FontCreator(new ClassPathResource("fonts/" + FontCreator.FONT_NAME));

    @Mock
    private MessageSourceUtil messageSourceUtil;

    @Test
    public void createPdf() throws IOException {
        PdfTableDataBuilder builder = PdfTableDataBuilder.create().withHeaderColumn("username").withHeaderColumn("correct results").withHeaderColumn("goal difference").withHeaderColumn("total points");
        builder.withColumnWidths(new float[]{3, 3, 3, 3}).withTitle("Fredbet Results").withLocale(Locale.getDefault());

        byte[] fileAsByteArray = pdfExportService.createPdfFileFrom(builder.build(), createTestData(), (rowContentAdder, row) -> {
            rowContentAdder.addCellContent(row.getUserName());
            rowContentAdder.addCellContent("" + row.getCorrectResultCount());
            rowContentAdder.addCellContent("" + row.getGoalDifference());
            rowContentAdder.addCellContent("" + row.getTotalPoints());
        });
        assertThat(fileAsByteArray).isNotNull();
        FileUtils.writeByteArrayToFile(new File("target/result.pdf"), fileAsByteArray);
    }

    @Test
    public void createPdfWithUmlauts() throws IOException {
        PdfTableDataBuilder builder = PdfTableDataBuilder.create().withHeaderColumn("października").withHeaderColumn("Prawidłowe zakłady").withHeaderColumn("Różnica goli");
        builder.withColumnWidths(new float[]{3, 3, 3}).withTitle("Fredbet Resultsäöü").withLocale(Locale.of("pl", "PL"));

        byte[] fileAsByteArray = pdfExportService.createPdfFileFrom(builder.build(), createTestData(), (rowContentAdder, row) -> {
            rowContentAdder.addCellContent(row.getUserName());
            rowContentAdder.addCellContent("" + row.getCorrectResultCount());
            rowContentAdder.addCellContent("" + row.getGoalDifference());
        });
        assertThat(fileAsByteArray).isNotNull();
        FileUtils.writeByteArrayToFile(new File("target/result.pdf"), fileAsByteArray);
    }

    @Test
    public void createMultiPageA4PdfWithRepeatedHeaderAndPageNumbers() throws IOException {
        when(messageSourceUtil.getMessageFor(eq("page"), any(Locale.class))).thenReturn("Page");

        PdfTableDataBuilder builder = PdfTableDataBuilder.create().withTitle("FredBet Ranking").withSubtitle("Adults only")
            .withLocale(Locale.ENGLISH).withHeaderColumn("#").withHeaderColumn("username", Element.ALIGN_LEFT).withHeaderColumn("total points")
            .withColumnWidths(new float[]{1, 5, 2}).withLegendEntry(Color.YELLOW, "Same position");

        List<Integer> rows = IntStream.rangeClosed(1, 120).boxed().toList();
        byte[] pdf = pdfExportService.createPdfFileFrom(builder.build(), rows, (rowContentAdder, row) -> {
            if (row % 10 == 0) {
                rowContentAdder.setRowBackground(Color.YELLOW);
            }
            rowContentAdder.addCellContent(String.valueOf(row), row == 1 ? Color.ORANGE : null);
            rowContentAdder.addCellContent("user" + row);
            rowContentAdder.addCellContent(String.valueOf(1000 - row));
        });
        FileUtils.writeByteArrayToFile(new File("target/result_multipage.pdf"), pdf);

        PdfReader reader = new PdfReader(pdf);
        int pages = reader.getNumberOfPages();
        assertThat(pages).isGreaterThan(1);
        assertThat(reader.getPageSize(1).getWidth()).isEqualTo(PageSize.A4.getWidth());
        assertThat(reader.getPageSize(1).getHeight()).isEqualTo(PageSize.A4.getHeight());

        PdfTextExtractor extractor = new PdfTextExtractor(reader);
        String firstPage = extractor.getTextFromPage(1);
        String lastPage = extractor.getTextFromPage(pages);
        // the total page count is written into a separate placeholder, so the text extraction may drop the whitespace before it
        assertThat(firstPage).contains("Adults only");
        assertThat(firstPage.replaceAll("\\s", "")).contains("Page1/" + pages);
        // the header row is repeated on the following pages
        assertThat(lastPage).contains("username", "user120", "Same position");
        assertThat(lastPage.replaceAll("\\s", "")).contains("Page" + pages + "/" + pages);
        reader.close();
    }

    private List<UsernamePoints> createTestData() {
        List<UsernamePoints> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            UsernamePoints usernamePoints = new UsernamePoints("Michael_üäö", 100);
            usernamePoints.setCorrectResultCount(23);
            usernamePoints.setGoalDifference(32);
            data.add(usernamePoints);
        }

        return data;
    }
}
