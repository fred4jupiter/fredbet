package de.fred4jupiter.fredbet.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

@Service
public class SystemInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

    private final Optional<BuildProperties> buildProperties;

    private final Environment environment;

    private final SortedMap<String, Object> allProperties = new TreeMap<>();

    private final Optional<GitProperties> gitProperties;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm Z");

    public SystemInfoService(Optional<BuildProperties> buildProperties, Environment environment, Optional<GitProperties> gitProperties) {
        this.buildProperties = buildProperties;
        this.environment = environment;
        this.gitProperties = gitProperties;
        addStaticProperties();
    }

    private void addStaticProperties() {
        buildProperties.ifPresent(props -> {
            add("Build: Build Time", props.getTime());
            add("Build: Build Version", props.getVersion());
            add("Build: Java Version", props.get("java.source"));
        });

        gitProperties.ifPresent(props -> {
            add("GIT: Commit ID", props.getCommitId());
            add("GIT: Branch", props.getBranch());
            add("GIT: Commit Message", props.get("commit.message.full"));
            add("GIT: Commit Time", props.getCommitTime());
        });

        addSpringProfiles();
        addEnvProperty("JDBC Driver Class", "spring.datasource.driver-class-name");
        addEnvProperty("JDBC-URL", "spring.datasource.url");
        addEnvProperty("Image Location", "fredbet.image-location");
        addEnvProperty("Image Size", "fredbet.image-size");
        addEnvProperty("Thumbnail Size", "fredbet.thumbnail-size");
    }

    public SortedMap<String, Object> fetchSystemInfo() {
        SortedMap<String, Object> props = new TreeMap<>(allProperties);
        props.put("Current Date", ZonedDateTime.now().format(dateTimeFormatter));
        props.put("Host Name", getHostName());
        props.put("Timezone", ZoneId.systemDefault().toString());
        return props;
    }

    private void addEnvProperty(String label, String envKey) {
        add(label, environment.getProperty(envKey));
    }

    private void addSpringProfiles() {
        add("Active Profiles", Arrays.asList(environment.getActiveProfiles()));
    }

    private void add(String key, Object value) {
        if (value != null) {
            allProperties.put(key, value);
        }
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
