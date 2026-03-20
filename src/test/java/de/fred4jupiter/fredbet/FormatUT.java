package de.fred4jupiter.fredbet;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class FormatUT {

    @Test
    public void parseDate() {
        LocalDateTime parse = LocalDateTime.parse("2015-03-31 18:55:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertThat(parse).isNotNull();
    }
}
