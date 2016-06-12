package de.fred4jupiter.fredbet.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtilsTest {

	private static final Logger LOG = LoggerFactory.getLogger(DateUtilsTest.class);

	@Test
	public void parseAndFormatTimestamp() {
		final String buildTimestamp = "2016-06-12 10:57 +0000";

		ZonedDateTime parsed = ZonedDateTime.parse(buildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm Z"));

		LOG.debug("formatted: {}", parsed
				.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm zZ", Locale.getDefault()).withZone(ZoneId.of("Europe/Berlin"))));
	}
}
