package de.fred4jupiter.fredbet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for date time convertion.
 * 
 * @author michael
 *
 */
public final class DateUtils {

	private DateUtils() {
		// only static methods
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return LocalDateTime.ofInstant(instant, getZoneId());
	}

	public static Date toDate(LocalDateTime localDateTime) {
		Instant instant = localDateTime.atZone(getZoneId()).toInstant();
		return Date.from(instant);
	}

	public static Date parseDate(String date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			return simpleDateFormat.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public static ZonedDateTime parseToZonedDateTime(String dateString, String pattern) {
		return ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern(pattern));
	}

	public static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zZ", Locale.getDefault());
		return zonedDateTime.format(dateTimeFormatter.withZone(getZoneId()));
	}

	public static String formatMillis(Long millis) {
		return String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	private static ZoneId getZoneId() {
		return ZoneId.systemDefault();
	}

	public static ZonedDateTime parseBuildTimestamp(String buildTimestamp) {
		return parseToZonedDateTime(buildTimestamp + " +0000", "yyyy-MM-dd HH:mm Z");
	}
}
