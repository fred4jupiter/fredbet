package de.fred4jupiter.fredbet.util;

import java.util.Collection;

import de.fred4jupiter.fredbet.domain.Country;

/**
 * Validation utility class.
 * 
 * @author michael
 *
 */
public final class Validator {

	private Validator() {
		// only static methods
	}

	/**
	 * Return {@code true} if the supplied Collection is {@code null} or empty.
	 * Otherwise, return {@code false}.
	 * 
	 * @param collection
	 *            the Collection to check
	 * @return whether the given Collection is empty
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	
	public static boolean isEmpty(Country country) {
		return country == null || Country.NONE.equals(country);
	}

	public static boolean isNotEmpty(Country country) {
		return !isEmpty(country);
	}
}
