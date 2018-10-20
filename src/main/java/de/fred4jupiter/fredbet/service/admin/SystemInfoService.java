package de.fred4jupiter.fredbet.service.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class SystemInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

    private final BuildProperties buildProperties;

    private final Environment environment;

    private final SortedMap<String, Object> allProperties = new TreeMap<>();

    @Autowired
    public SystemInfoService(BuildProperties buildProperties, Environment environment) {
        this.buildProperties = buildProperties;
        this.environment = environment;

        addStaticProperties();
    }

    private void addStaticProperties() {
        allProperties.put("build.time", buildProperties.getTime());
        allProperties.put("build.version", buildProperties.getVersion());

        addSpringProfiles();
        addEnvProperty("spring.datasource.hikari.driver-class-name");
        addEnvProperty("spring.datasource.hikari.jdbc-url");
        addEnvProperty("fredbet.image-location");
        addEnvProperty("fredbet.image-size");
        addEnvProperty("fredbet.thumbnail-size");
        addEnvProperty("fredbet.aws-s3bucket-name");
        addEnvProperty("fredbet.aws-region");
    }

    public SortedMap<String, Object> fetchSystemInfo() {
        SortedMap<String, Object> props = new TreeMap<>(allProperties);
        props.put("currentDateTime", ZonedDateTime.now());
        props.put("hostName", getHostName());
        props.put("system.timeZone", ZoneId.systemDefault().toString());
        return props;
    }

    private void addEnvProperty(String envKey) {
        allProperties.put(envKey, environment.getProperty(envKey));
    }

    private void addSpringProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();
        allProperties.put("Active Profiles", Arrays.asList(activeProfiles));
    }

    private String getHostName() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
            return "n.A.";
        }
    }
}
