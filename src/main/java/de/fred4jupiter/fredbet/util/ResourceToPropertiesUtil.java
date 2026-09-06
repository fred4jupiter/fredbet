package de.fred4jupiter.fredbet.util;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ResourceToPropertiesUtil {

    public static Properties loadCountryNames(Resource countryNameResource) {
        try (final InputStream in = countryNameResource.getInputStream()) {
            final Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
