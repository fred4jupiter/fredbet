package de.fred4jupiter.fredbet.service.pdf;

import de.fred4jupiter.fredbet.repository.UsernamePoints;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PdfExportServiceUT {

    @InjectMocks
    private PdfExportService pdfExportService;

    @Test
    public void createPdf() throws IOException {
        String[] headerColumns = new String[]{"username", "correctResultCount", "goalDifference", "totalPoints"};

        UsernamePoints usernamePoints = new UsernamePoints();
        usernamePoints.setUserName("Michael");
        usernamePoints.setTotalPoints(100);
        usernamePoints.setCorrectResultCount(23);
        usernamePoints.setGoalDifference(32);

        byte[] fileAsByteArray = pdfExportService.createPdfFileFrom(headerColumns, Collections.singletonList(usernamePoints), (rowContentAdder, row) -> {
            rowContentAdder.addCellContent(row.getUserName());
            rowContentAdder.addCellContent("" + row.getCorrectResultCount());
            rowContentAdder.addCellContent("" + row.getGoalDifference());
            rowContentAdder.addCellContent("" + row.getTotalPoints());
        });
        assertThat(fileAsByteArray).isNotNull();
        FileUtils.writeByteArrayToFile(new File("target/result.pdf"), fileAsByteArray);
    }
}
