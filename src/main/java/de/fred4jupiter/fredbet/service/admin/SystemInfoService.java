package de.fred4jupiter.fredbet.service.admin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.util.DateUtils;

@Service
public class SystemInfoService {

	private static final Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

	@Autowired
	private Properties buildProperties;

	@Autowired
	private MetricsEndpoint metricsEndpoint;

	@Autowired
	private Environment environment;

	private final SortedMap<String, Object> allProperties = new TreeMap<>();

	@PostConstruct
	private void addStaticProperties() {
		for (final String name : buildProperties.stringPropertyNames()) {
			allProperties.put(name, buildProperties.getProperty(name));
		}
		addBuildTimestamp(allProperties);
		addSpringProfiles(allProperties);
		addEnvProperty("fredbet.database-type", allProperties);
		addEnvProperty("fredbet.image-location", allProperties);
		addEnvProperty("fredbet.image-size", allProperties);
		addEnvProperty("fredbet.thumbnail-size", allProperties);
		addEnvProperty("fredbet.aws-s3bucket-name", allProperties);
		addEnvProperty("fredbet.database-url", allProperties);

		addEnvProperty("cloud.aws.credentials.profileName", allProperties);
		addEnvProperty("cloud.aws.region.static", allProperties);
	}

	public SortedMap<String, Object> fetchSystemInfo() {
		addCurrentDateTime(allProperties);
		addHostName(allProperties);
		addMetrics(allProperties);
		allProperties.put("system.timeZone", ZoneId.systemDefault().toString());
		return allProperties;
	}

	private void addEnvProperty(String envKey, SortedMap<String, Object> map) {
		map.put(envKey, environment.getProperty(envKey));
	}

	private void addBuildTimestamp(SortedMap<String, Object> map) {
		String buildTimestamp = buildProperties.getProperty("build.timestamp");
		String convertedTimestamp = convertToSystemLocale(buildTimestamp);
		map.put("build.timestamp", convertedTimestamp);
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

	private void addMetrics(SortedMap<String, Object> map) {
		Map<String, Object> metricsMap = metricsEndpoint.invoke();
		Long systemUpdateMillis = (Long) metricsMap.get("uptime");
		map.put("system.uptime", DateUtils.formatMillis(systemUpdateMillis));
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
