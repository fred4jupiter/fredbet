package de.fred4jupiter.fredbet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

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
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		return days + " days " + hours % 24 + " hours " + minutes % 60 + " min " + seconds % 60 + " sec";
	}

	private static ZoneId getZoneId() {
		return ZoneId.systemDefault();
	}

	public static ZonedDateTime parseToZonedDateTime(Date date) {
		return date.toInstant().atZone(getZoneId());
	}

	public static String formatByLocale(Date date, Locale locale) {
		return formatByLocale(parseToZonedDateTime(date), locale);
	}

	public static String formatByLocale(ZonedDateTime zonedDateTime, Locale locale) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale);
		return zonedDateTime.format(dateTimeFormatter);
	}
}
