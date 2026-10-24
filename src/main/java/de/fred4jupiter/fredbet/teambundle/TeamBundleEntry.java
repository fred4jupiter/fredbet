package de.fred4jupiter.fredbet.teambundle;

/**
 * A team that can be part of a team bundle, e.g. a country or a club team.
 */
public interface TeamBundleEntry {

    String name();

    TeamBundleEntryType getType();

    /**
     * Key of the translated name in the message bundle.
     */
    String getMessageKey();

    /**
     * Unique name over all types, e.g. COUNTRY:GERMANY. Can be resolved with {@link TeamBundleEntryType#fromQualifiedName(String)}.
     */
    default String getQualifiedName() {
        return getType().name() + TeamBundleEntryType.QUALIFIED_NAME_SEPARATOR + name();
    }
}
