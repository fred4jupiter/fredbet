package de.fred4jupiter.fredbet.service.pdf;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@UnitTest
public class PdfExportServiceUT {

    @InjectMocks
    private PdfExportService pdfExportService;

    @Spy
    private FontCreator fontCreator = new FontCreator();

    @Mock
    private MessageSourceUtil messageSourceUtil;

    @Test
    public void createPdf() throws IOException {
        when(messageSourceUtil.getMessageFor(eq("page"), eq(Locale.getDefault()))).thenReturn("Seite");

        PdfTableDataBuilder builder = PdfTableDataBuilder.create().withHeaderColumn("username").withHeaderColumn("correct results").withHeaderColumn("goal difference").withHeaderColumn("total points");
        builder.withColumnWidths(new float[]{3, 3, 3, 3}).withTitle("Fredbet Results").withLocale(Locale.getDefault());

        byte[] fileAsByteArray = pdfExportService.createPdfFileFrom(builder, createTestData(), (rowContentAdder, row) -> {
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
        builder.withColumnWidths(new float[]{3, 3, 3}).withTitle("Fredbet Resultsäöü").withLocale(new Locale("pl", "PL"));

        byte[] fileAsByteArray = pdfExportService.createPdfFileFrom(builder, createTestData(), (rowContentAdder, row) -> {
            rowContentAdder.addCellContent(row.getUserName());
            rowContentAdder.addCellContent("" + row.getCorrectResultCount());
            rowContentAdder.addCellContent("" + row.getGoalDifference());
        });
        assertThat(fileAsByteArray).isNotNull();
        FileUtils.writeByteArrayToFile(new File("target/result.pdf"), fileAsByteArray);
    }

    private List<UsernamePoints> createTestData() {
        List<UsernamePoints> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            UsernamePoints usernamePoints = new UsernamePoints();
            usernamePoints.setUserName("Michael_üäö");
            usernamePoints.setTotalPoints(100);
            usernamePoints.setCorrectResultCount(23);
            usernamePoints.setGoalDifference(32);
            data.add(usernamePoints);
        }

        return data;
    }
}
