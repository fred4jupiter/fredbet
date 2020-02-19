package de.fred4jupiter.fredbet.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FormatUT {

    @Test
    public void parseDate() {
        LocalDateTime parse = LocalDateTime.parse("2015-03-31 18:55:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        assertNotNull(parse);
    }
}
