package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.domain.Country;

import java.util.Collection;

/**
 * Validation utility class.
 *
 * @author michael
 */
public final class Validator {

    private Validator() {
        // only static methods
    }

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     *
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNull(Country country) {
        return country == null || Country.NONE.equals(country);
    }

    public static boolean isNotNull(Country country) {
        return !isNull(country);
    }
}
