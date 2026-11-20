package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves a team name of a CSV file to a {@link TeamBundleEntry} (country or club team). A team name is resolved by
 * <ul>
 *     <li>the enum name, e.g. GERMANY or BAYERN_MUENCHEN</li>
 *     <li>the qualified name, e.g. COUNTRY:GERMANY</li>
 *     <li>the ISO 3166 alpha-3 code of a country, e.g. DEU</li>
 *     <li>the translated name in any supported language, e.g. Germany, Deutschland or Allemagne</li>
 * </ul>
 * The comparison ignores case, accents, spaces and punctuation.
 */
@Component
class TeamNameResolver {

    private final Map<String, TeamBundleEntry> entriesByName = new HashMap<>();

    TeamNameResolver() {
        List<TeamBundleEntry> allEntries = Arrays.stream(TeamBundleEntryType.values())
            .flatMap(type -> type.getEntries().stream())
            .toList();

        allEntries.forEach(entry -> register(entry.name(), entry));
        allEntries.stream()
            .filter(Country.class::isInstance)
            .map(Country.class::cast)
            .forEach(country -> register(country.getAlpha3Code(), country));

        Map<String, TeamBundleEntry> entriesByMessageKey = allEntries.stream()
            .collect(Collectors.toMap(TeamBundleEntry::getMessageKey, Function.identity(), (first, _) -> first));

        for (Properties translations : TranslationLookup.loadAllLanguages("classpath*:msgs/TeamKey_*.properties")) {
            translations.forEach((key, value) -> {
                TeamBundleEntry entry = entriesByMessageKey.get((String) key);
                if (entry != null) {
                    register((String) value, entry);
                }
            });
        }
    }

    private void register(String name, TeamBundleEntry entry) {
        String normalized = TranslationLookup.normalize(name);
        if (!normalized.isEmpty()) {
            entriesByName.putIfAbsent(normalized, entry);
        }
    }

    /**
     * @return the matching team bundle entry or null, if the team name is unknown
     */
    TeamBundleEntry resolve(String teamName) {
        if (StringUtils.isBlank(teamName)) {
            return null;
        }

        TeamBundleEntry byQualifiedName = resolveQualifiedName(teamName.trim());
        if (byQualifiedName != null) {
            return byQualifiedName;
        }

        return entriesByName.get(TranslationLookup.normalize(teamName));
    }

    private TeamBundleEntry resolveQualifiedName(String teamName) {
        if (!teamName.contains(":")) {
            return null;
        }
        try {
            return TeamBundleEntryType.fromQualifiedName(teamName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
