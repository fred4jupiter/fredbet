package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Parses the kick-off date of a CSV file. If no date format is given, common formats are tried. A date with a time
 * zone or offset (e.g. 2026-06-11T19:00:00Z) is converted into the time zone of the runtime settings.
 */
@Component
class KickOffDateParser {

    private static final List<DateTimeFormatter> LOCAL_FORMATS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]"),
        DateTimeFormatter.ofPattern("d.M.yyyy H:mm[:ss]"),
        DateTimeFormatter.ofPattern("d.M.yy H:mm[:ss]"),
        DateTimeFormatter.ofPattern("d/M/yyyy H:mm[:ss]")
    );

    private final RuntimeSettingsService runtimeSettingsService;

    KickOffDateParser(RuntimeSettingsService runtimeSettingsService) {
        this.runtimeSettingsService = runtimeSettingsService;
    }

    /**
     * @param dateFormat optional pattern of {@link DateTimeFormatter}, e.g. dd.MM.yyyy HH:mm
     * @return the parsed kick-off date or null, if the value could not be parsed
     * @throws IllegalArgumentException if the given date format is no valid pattern
     */
    LocalDateTime parse(String value, String dateFormat) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        String trimmed = value.trim();
        if (StringUtils.isNotBlank(dateFormat)) {
            return parseLocal(trimmed, DateTimeFormatter.ofPattern(dateFormat.trim(), Locale.ENGLISH));
        }

        LocalDateTime zoned = parseZoned(trimmed);
        if (zoned != null) {
            return zoned;
        }

        return LOCAL_FORMATS.stream()
            .map(formatter -> parseLocal(trimmed, formatter))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    /**
     * Validates the given date format.
     *
     * @return true if the date format is empty (auto-detection) or a valid pattern
     */
    boolean isValidDateFormat(String dateFormat) {
        if (StringUtils.isBlank(dateFormat)) {
            return true;
        }
        try {
            DateTimeFormatter.ofPattern(dateFormat.trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private LocalDateTime parseLocal(String value, DateTimeFormatter formatter) {
        try {
            return LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalDateTime parseZoned(String value) {
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(value);
            return offsetDateTime.atZoneSameInstant(targetZone()).toLocalDateTime();
        } catch (DateTimeParseException e) {
            // try next format
        }

        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(value);
            return zonedDateTime.withZoneSameInstant(targetZone()).toLocalDateTime();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private ZoneId targetZone() {
        return ZoneId.of(runtimeSettingsService.loadRuntimeSettings().getTimeZone());
    }
}
