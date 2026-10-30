package de.fred4jupiter.fredbet.admin;


import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class SystemInfoServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoServiceIT.class);

    @Autowired
    private SystemInfoService systemInfoService;

    @Test
    public void fetchSystemInfo() {
        Map<String, Map<String, Object>> sections = systemInfoService.fetchSystemInfo();
        assertThat(sections).containsKeys("Configuration", "Runtime");
        assertThat(sections.get("Runtime")).containsKeys("Current Date", "Timezone", "Host Name", "Current User");
        sections.forEach((section, properties) -> properties.forEach((label, value) -> LOG.debug("{} - {}: {}", section, label, value)));
    }

}
