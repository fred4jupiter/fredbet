package de.fred4jupiter.fredbet.props;

/**
 * Please NOTE: Add the cache name in config. Application -> CacheManager
 * 
 * 
 * @author michael
 *
 */
public interface CacheNames {

	/**
	 * For showing the right navigation entries.
	 */
	String AVAIL_GROUPS = "availableGroups";

	/**
	 * Information about which user is grown up or child.
	 */
	String CHILD_RELATION = "childRelation";

	/**
	 * Runtime config parameters that will be cached.
	 */
	String RUNTIME_CONFIG = "runtimeConfig";
}
