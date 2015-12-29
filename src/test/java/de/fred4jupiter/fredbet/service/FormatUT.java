package de.fred4jupiter.fredbet.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import static org.junit.Assert.*;

public class FormatUT {

	@Test
	public void parseDate() {
		LocalDateTime parse = LocalDateTime.parse("2015-03-31 18:55:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		assertNotNull(parse);
	}
}
