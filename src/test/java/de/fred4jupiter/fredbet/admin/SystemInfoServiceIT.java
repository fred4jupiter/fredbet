package de.fred4jupiter.fredbet.admin;


import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map.Entry;
import java.util.SortedMap;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class SystemInfoServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoServiceIT.class);

    @Autowired
    private SystemInfoService systemInfoService;

    @Test
    public void fetchSystemInfo() {
        SortedMap<String, Object> map = systemInfoService.fetchSystemInfo();
        assertThat(map).isNotNull();
        for (Entry<String, Object> entry : map.entrySet()) {
            LOG.debug("{}: {}", entry.getKey(), entry.getValue());
        }
    }

}
