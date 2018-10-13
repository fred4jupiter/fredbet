package de.fred4jupiter.fredbet.service.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.InfoProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class SystemInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

    @Autowired
    private BuildProperties buildProperties;

    @Autowired
    private Environment environment;

    private final SortedMap<String, Object> allProperties = new TreeMap<>();

    @PostConstruct
    private void addStaticProperties() {
        Iterator<InfoProperties.Entry> iterator = buildProperties.iterator();
        while (iterator.hasNext()) {
            InfoProperties.Entry entry = iterator.next();
            allProperties.put(entry.getKey(), entry.getValue());
        }

        addSpringProfiles(allProperties);
        addEnvProperty("spring.datasource.hikari.driver-class-name", allProperties);
        addEnvProperty("spring.datasource.hikari.jdbc-url", allProperties);
        addEnvProperty("fredbet.image-location", allProperties);
        addEnvProperty("fredbet.image-size", allProperties);
        addEnvProperty("fredbet.thumbnail-size", allProperties);
        addEnvProperty("fredbet.aws-s3bucket-name", allProperties);
        addEnvProperty("fredbet.aws-region", allProperties);
    }

    public SortedMap<String, Object> fetchSystemInfo() {
        addCurrentDateTime(allProperties);
        addHostName(allProperties);
        allProperties.put("system.timeZone", ZoneId.systemDefault().toString());
        return allProperties;
    }

    private void addEnvProperty(String envKey, SortedMap<String, Object> map) {
        map.put(envKey, environment.getProperty(envKey));
    }

    private String convertToSystemLocale(String buildTimestamp) {
        String tmpBuildTimestamp = buildTimestamp + " +00:00";
        ZonedDateTime parseToZonedDateTime = ZonedDateTime.parse(tmpBuildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm ZZZZZ"));
        ZonedDateTime converted = parseToZonedDateTime.withZoneSameInstant(ZoneOffset.systemDefault());
        return converted.toString();
    }

    private void addSpringProfiles(SortedMap<String, Object> map) {
        String[] activeProfiles = environment.getActiveProfiles();
        map.put("Active Profiles", activeProfiles != null ? Arrays.asList(activeProfiles) : "");
    }

    private void addHostName(SortedMap<String, Object> map) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostName = localHost.getHostName();
            map.put("hostName", hostName);
        } catch (UnknownHostException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void addCurrentDateTime(SortedMap<String, Object> map) {
        map.put("currentDateTime", ZonedDateTime.now());
    }
}
