package de.fred4jupiter.fredbet.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateUtilsUT {

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

}
