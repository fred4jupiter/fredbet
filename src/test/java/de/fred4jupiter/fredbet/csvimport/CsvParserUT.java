package de.fred4jupiter.fredbet.csvimport;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CsvParserUT {

    @Test
    public void parseCommaSeparated() {
        CsvTable table = parse("Home,Away,Date\nGermany,France,2026-06-11 21:00\nSpain,Italy,2026-06-12 18:00\n");

        assertThat(table.headers()).containsExactly("Home", "Away", "Date");
        assertThat(table.rows()).containsExactly(
            List.of("Germany", "France", "2026-06-11 21:00"),
            List.of("Spain", "Italy", "2026-06-12 18:00"));
    }

    @Test
    public void detectSemicolonAndHandleQuotes() {
        CsvTable table = parse("﻿Home;Away;Venue\r\n\"Germany\";France;\"Stadium \"\"A\"\"; Berlin\"\r\n");

        assertThat(table.headers()).containsExactly("Home", "Away", "Venue");
        assertThat(table.rows()).containsExactly(List.of("Germany", "France", "Stadium \"A\"; Berlin"));
    }

    @Test
    public void detectTabAndFillMissingColumns() {
        CsvTable table = parse("Home\tAway\tVenue\nGermany\tFrance\n\n");

        assertThat(table.rows()).containsExactly(List.of("Germany", "France", ""));
    }

    @Test
    public void emptyFileIsRejected() {
        assertThatThrownBy(() -> parse("  \n")).isInstanceOf(CsvImportException.class)
            .extracting(e -> ((CsvImportException) e).getMsgKey()).isEqualTo("csvimport.msg.emptyFile");
    }

    @Test
    public void fileWithoutDataRowsIsRejected() {
        assertThatThrownBy(() -> parse("Home,Away\n")).isInstanceOf(CsvImportException.class)
            .extracting(e -> ((CsvImportException) e).getMsgKey()).isEqualTo("csvimport.msg.noDataRows");
    }

    @Test
    public void unclosedQuoteIsRejected() {
        assertThatThrownBy(() -> parse("Home,Away\n\"Germany,France\n")).isInstanceOf(CsvImportException.class)
            .extracting(e -> ((CsvImportException) e).getMsgKey()).isEqualTo("csvimport.msg.unclosedQuote");
    }

    private CsvTable parse(String content) {
        return CsvParser.parse(content.getBytes(StandardCharsets.UTF_8));
    }
}
