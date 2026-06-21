package de.fred4jupiter.fredbet.util;

import ch.qos.logback.classic.Level;
import de.fred4jupiter.fredbet.Application;
import de.fred4jupiter.fredbet.common.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class UtilityClassesUT {

    private final LoggingUtil loggingUtil = new LoggingUtil();

    private final TimeZoneUtil timeZoneUtil = new TimeZoneUtil();

    private final ch.qos.logback.classic.Logger appLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Application.class.getPackageName());

    private final Level originalLevel = appLogger.getLevel();

    private final TimeZone originalTimeZone = TimeZone.getDefault();

    @AfterEach
    public void tearDown() {
        appLogger.setLevel(originalLevel);
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    public void setLogLevelAndReadCurrentLogLevel() {
        loggingUtil.setLogLevelTo(LogLevel.WARN);

        assertThat(appLogger.getLevel()).isEqualTo(Level.WARN);
        assertThat(loggingUtil.getCurrentLogLevel()).isEqualTo(LogLevel.WARN);
    }

    @Test
    public void createResponseEntityReturnsNotFoundWhenBinaryIsNull() {
        ResponseEntity<byte[]> responseEntity = ResponseEntityUtil.createResponseEntity("test.txt", null, "text/plain");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createResponseEntityBuildsAttachmentHeaders() {
        byte[] content = "fredbet".getBytes();

        ResponseEntity<byte[]> responseEntity = ResponseEntityUtil.createResponseEntity("report.pdf", content, "application/pdf");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getFirst("Content-Type")).isEqualTo("application/pdf");
        assertThat(responseEntity.getHeaders().getFirst("Content-Disposition")).isEqualTo("attachment; filename=\"report.pdf\"");
        assertThat(responseEntity.getBody()).isEqualTo(content);
    }

    @Test
    public void createResponseEntitySupportsInlineDownloads() {
        ResponseEntity<byte[]> responseEntity = ResponseEntityUtil.createResponseEntity(
            "image.jpg", new byte[]{1, 2}, "image/jpeg", ResponseEntityUtil.DownloadType.INLINE);

        assertThat(responseEntity.getHeaders().getFirst("Content-Disposition")).isEqualTo("inline; filename=\"image.jpg\"");
    }

    @Test
    public void checkIfTimezoneIsCorrectSetsRequestedTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        timeZoneUtil.checkIfTimezoneIsCorrect("Europe/Berlin");

        assertThat(TimeZone.getDefault().getID()).isEqualTo("Europe/Berlin");
    }

    @Test
    public void blankTimezoneDoesNotOverrideCurrentTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        timeZoneUtil.setDefaultTimeZone(" ");

        assertThat(TimeZone.getDefault().getID()).isEqualTo("UTC");
    }
}

