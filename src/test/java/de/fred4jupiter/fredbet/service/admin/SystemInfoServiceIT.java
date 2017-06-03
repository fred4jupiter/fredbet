package de.fred4jupiter.fredbet.service.admin;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;

public class SystemInfoServiceIT extends AbstractIntegrationTest {

	private static final Logger LOG = LoggerFactory.getLogger(SystemInfoServiceIT.class);

	@Autowired
	private SystemInfoService systemInfoService;

	@Test
	public void fetchSystemInfo() {
		SortedMap<String, Object> map = systemInfoService.fetchSystemInfo();
		assertNotNull(map);
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			LOG.debug("{}: {}", entry.getKey(), entry.getValue());			
		}
	}

}
