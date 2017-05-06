package de.fred4jupiter.fredbet.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtilsUT {

	private static final Logger LOG = LoggerFactory.getLogger(DateUtilsUT.class);

	@Test
	public void parseCurrentDateAsMillis() {
		long nowAsMillis = System.currentTimeMillis();
		String nowAsString = DateUtils.formatMillis(nowAsMillis);
		LOG.debug("nowAsString={}", nowAsString);
	}

}
