package de.fred4jupiter.fredbet.csvimport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Helper to look up the translations of a message bundle in all languages, so that values of a CSV file can be resolved
 * independent of the language they are written in.
 */
final class TranslationLookup {

    private TranslationLookup() {
    }

    /**
     * Loads the message bundles of all languages that match the given pattern, e.g. classpath*:msgs/TeamKey_*.properties.
     */
    static List<Properties> loadAllLanguages(String locationPattern) {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(locationPattern);
            List<Properties> result = new ArrayList<>();
            for (Resource resource : resources) {
                result.add(load(resource));
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load message bundles " + locationPattern, e);
        }
    }

    private static Properties load(Resource resource) throws IOException {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        }
    }

    /**
     * Normalizes a value for a lenient comparison: lower case, without accents and only letters and digits, e.g.
     * "Côte d'Ivoire" becomes "cotedivoire".
     */
    static String normalize(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).replace("ß", "ss").replaceAll("[^\\p{L}\\p{N}]", "");
    }
}
