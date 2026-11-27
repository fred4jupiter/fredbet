package de.fred4jupiter.fredbet.csvimport;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple CSV parser (RFC 4180) with support for quoted values, line breaks within quotes and escaped quotes ("").
 * The delimiter (comma, semicolon or tab) is detected from the header line.
 */
final class CsvParser {

    private static final char QUOTE = '"';

    private static final char[] DELIMITERS = {',', ';', '\t'};

    private CsvParser() {
    }

    static CsvTable parse(byte[] content) {
        if (content == null || content.length == 0) {
            throw new CsvImportException("csvimport.msg.emptyFile", "CSV file is empty!");
        }

        String text = removeByteOrderMark(new String(content, StandardCharsets.UTF_8));
        char delimiter = detectDelimiter(text);
        List<List<String>> lines = parseLines(text, delimiter).stream().filter(line -> !isBlankLine(line)).toList();
        if (lines.isEmpty()) {
            throw new CsvImportException("csvimport.msg.emptyFile", "CSV file is empty!");
        }

        List<String> headers = lines.getFirst().stream().map(String::trim).toList();
        if (lines.size() < 2) {
            throw new CsvImportException("csvimport.msg.noDataRows", "CSV file contains no data rows!");
        }

        List<List<String>> rows = lines.subList(1, lines.size()).stream().map(row -> toColumnCount(row, headers.size())).toList();
        return new CsvTable(headers, rows);
    }

    private static String removeByteOrderMark(String text) {
        return text.startsWith("﻿") ? text.substring(1) : text;
    }

    /**
     * The delimiter that occurs most often in the header line (outside of quotes).
     */
    private static char detectDelimiter(String text) {
        String headerLine = StringUtils.substringBefore(text, "\n");
        char result = DELIMITERS[0];
        int maxCount = 0;
        for (char delimiter : DELIMITERS) {
            int count = countOutsideQuotes(headerLine, delimiter);
            if (count > maxCount) {
                maxCount = count;
                result = delimiter;
            }
        }
        return result;
    }

    private static int countOutsideQuotes(String line, char delimiter) {
        int count = 0;
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == QUOTE) {
                inQuotes = !inQuotes;
            } else if (c == delimiter && !inQuotes) {
                count++;
            }
        }
        return count;
    }

    private static List<List<String>> parseLines(String text, char delimiter) {
        List<List<String>> lines = new ArrayList<>();
        List<String> currentLine = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inQuotes) {
                if (c == QUOTE && i + 1 < text.length() && text.charAt(i + 1) == QUOTE) {
                    value.append(QUOTE);
                    i++;
                } else if (c == QUOTE) {
                    inQuotes = false;
                } else {
                    value.append(c);
                }
            } else if (c == QUOTE) {
                inQuotes = true;
            } else if (c == delimiter) {
                currentLine.add(value.toString());
                value.setLength(0);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                }
                currentLine.add(value.toString());
                value.setLength(0);
                lines.add(currentLine);
                currentLine = new ArrayList<>();
            } else {
                value.append(c);
            }
        }

        if (inQuotes) {
            throw new CsvImportException("csvimport.msg.unclosedQuote", "CSV file contains an unclosed quote!");
        }

        if (!value.isEmpty() || !currentLine.isEmpty()) {
            currentLine.add(value.toString());
            lines.add(currentLine);
        }
        return lines;
    }

    private static boolean isBlankLine(List<String> line) {
        return line.stream().allMatch(StringUtils::isBlank);
    }

    private static List<String> toColumnCount(List<String> row, int columnCount) {
        List<String> result = new ArrayList<>(row.subList(0, Math.min(row.size(), columnCount)));
        result.addAll(Collections.nCopies(columnCount - result.size(), ""));
        return result.stream().map(String::trim).toList();
    }
}
