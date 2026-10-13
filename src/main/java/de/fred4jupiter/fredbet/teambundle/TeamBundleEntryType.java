package de.fred4jupiter.fredbet.teambundle;

import de.fred4jupiter.fredbet.domain.ClubTeam;
import de.fred4jupiter.fredbet.domain.Country;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * The kind of a team bundle entry. Resolves the persisted name back to the matching enum constant.
 */
public enum TeamBundleEntryType {

    COUNTRY(Country::valueOf, Country.values()),

    CLUB(ClubTeam::valueOf, ClubTeam.values());

    static final String QUALIFIED_NAME_SEPARATOR = ":";

    private final Function<String, TeamBundleEntry> resolver;

    private final List<TeamBundleEntry> entries;

    TeamBundleEntryType(Function<String, TeamBundleEntry> resolver, TeamBundleEntry[] entries) {
        this.resolver = resolver;
        this.entries = List.of(entries);
    }

    public TeamBundleEntry resolve(String name) {
        return resolver.apply(name);
    }

    public List<TeamBundleEntry> getEntries() {
        return entries;
    }

    /**
     * Resolves a qualified name like COUNTRY:GERMANY (see {@link TeamBundleEntry#getQualifiedName()}).
     */
    public static TeamBundleEntry fromQualifiedName(String qualifiedName) {
        if (StringUtils.isBlank(qualifiedName)) {
            return null;
        }

        String type = StringUtils.substringBefore(qualifiedName, QUALIFIED_NAME_SEPARATOR);
        String name = StringUtils.substringAfter(qualifiedName, QUALIFIED_NAME_SEPARATOR);
        return valueOf(type).resolve(name);
    }
}
