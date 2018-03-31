package de.fred4jupiter.fredbet.util;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtilsUT {

	private static final Logger LOG = LoggerFactory.getLogger(DateUtilsUT.class);

	@Test
	public void parseMillis() {
		// 8 hours, 30 min
		long timeInMillis = ((8 * 60) + 30) * 60 * 1000;
		assertEquals("0 days 8 hours 30 min 0 sec", DateUtils.formatMillis(timeInMillis));
	}

	@Test
	public void parseMillis2() {
		// 25 hours, 54 min
		long timeInMillis = ((25 * 60) + 54) * 60 * 1000;
		assertEquals("1 days 1 hours 54 min 0 sec", DateUtils.formatMillis(timeInMillis));
	}

	@Test
	public void formatByLocaleDE() {
		LocalDateTime localDateTime = LocalDateTime.of(2017, 5, 20, 20, 25);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC+02:00"));
		assertEquals("20.05.2017, 20:25:00", DateUtils.formatByLocale(zonedDateTime, Locale.GERMAN));
	}

	@Test
	public void formatByLocaleEN() {
		LocalDateTime localDateTime = LocalDateTime.of(2017, 5, 20, 20, 25);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC+02:00"));
		assertEquals("May 20, 2017, 8:25:00 PM", DateUtils.formatByLocale(zonedDateTime, Locale.ENGLISH));
	}

	@Test
	public void parseBuildTimestampToLocalDateTime() {
		String buildTimestamp = "2018-01-01 17:01 +00:00";

		ZonedDateTime parseToZonedDateTime = ZonedDateTime.parse(buildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm ZZZZZ"));
		LOG.debug("parseToZonedDateTime: {}", parseToZonedDateTime);

		ZonedDateTime converted = parseToZonedDateTime.withZoneSameInstant(ZoneOffset.systemDefault());
		LOG.debug("converted: {}", converted);
	}
}
