package de.fred4jupiter.fredbet.props;

/**
 * Please NOTE: Add the cache name in config. See class CacheConfig.
 *
 * @author michael
 */
public final class CacheNames {

    private CacheNames() {
    }

    /**
     * For showing the right navigation entries.
     */
    public static final String AVAIL_GROUPS = "availableGroups";

    /**
     * Information about which user is grown up or child.
     */
    public static final String CHILD_RELATION = "childRelation";

    /**
     * Runtime config parameters that will be cached.
     */
    public static final String RUNTIME_SETTINGS = "runtimeSettings";

    /**
     * Runtime config parameters for points.
     */
    public static final String POINTS_CONFIG = "pointsConfig";
}
