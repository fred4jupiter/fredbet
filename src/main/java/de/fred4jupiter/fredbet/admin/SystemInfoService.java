package de.fred4jupiter.fredbet.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

    private final Optional<BuildProperties> buildProperties;

    private final Environment environment;

    private static final String SECTION_BUILD = "Build";
    private static final String SECTION_GIT = "Git";
    private static final String SECTION_CONFIGURATION = "Configuration";
    private static final String SECTION_RUNTIME = "Runtime";

    // section -> (label -> value), both in the order they are added
    private final Map<String, Map<String, Object>> staticProperties = new LinkedHashMap<>();

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
            add(SECTION_BUILD, "Version", props.getVersion());
            add(SECTION_BUILD, "Build Time", format(props.getTime()));
            add(SECTION_BUILD, "Java Version", props.get("build.java.version"));
        });

        gitProperties.ifPresent(props -> {
            add(SECTION_GIT, "Branch", props.getBranch());
            add(SECTION_GIT, "Commit ID", props.getCommitId());
            add(SECTION_GIT, "Commit Time", format(props.getCommitTime()));
            add(SECTION_GIT, "Commit Message", props.get("commit.message.full"));
        });

        add(SECTION_CONFIGURATION, "Active Profiles", String.join(", ", environment.getActiveProfiles()));
        addEnvProperty("JDBC Driver Class", "spring.datasource.driver-class-name");
        addEnvProperty("JDBC-URL", "spring.datasource.url");
        addEnvProperty("Image Location", "fredbet.image-location");
        addEnvProperty("Image Size", "fredbet.image-size");
        addEnvProperty("Thumbnail Size", "fredbet.thumbnail-size");
    }

    /**
     * @return the system info grouped by section (build, git, configuration, runtime)
     */
    public Map<String, Map<String, Object>> fetchSystemInfo() {
        Map<String, Map<String, Object>> sections = new LinkedHashMap<>();
        staticProperties.forEach((section, properties) -> sections.put(section, new LinkedHashMap<>(properties)));

        Map<String, Object> runtime = sections.computeIfAbsent(SECTION_RUNTIME, key -> new LinkedHashMap<>());
        runtime.put("Current Date", ZonedDateTime.now().format(dateTimeFormatter));
        runtime.put("Timezone", ZoneId.systemDefault().toString());
        runtime.put("Host Name", getHostName());
        runtime.put("Current User", System.getProperty("user.name"));
        return sections;
    }

    private String format(Instant instant) {
        return instant != null ? instant.atZone(ZoneId.systemDefault()).format(dateTimeFormatter) : null;
    }

    private void addEnvProperty(String label, String envKey) {
        add(SECTION_CONFIGURATION, label, environment.getProperty(envKey));
    }

    private void add(String section, String label, Object value) {
        if (value != null && !value.toString().isBlank()) {
            staticProperties.computeIfAbsent(section, key -> new LinkedHashMap<>()).put(label, value);
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
