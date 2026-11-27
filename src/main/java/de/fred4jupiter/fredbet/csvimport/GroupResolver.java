package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.domain.Group;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Resolves the group of a CSV file to a {@link Group}. A group is resolved by
 * <ul>
 *     <li>the enum name, e.g. GROUP_A or ROUND_OF_SIXTEEN</li>
 *     <li>the letter of a group stage group, e.g. A</li>
 *     <li>the stage names of football-data.org, e.g. LAST_16 or QUARTER_FINALS</li>
 *     <li>the translated name in any supported language, e.g. Group A, Gruppe A or Achtelfinale</li>
 * </ul>
 * The comparison ignores case, accents, spaces and punctuation.
 */
@Component
class GroupResolver {

    private final Map<String, Group> groupsByName = new HashMap<>();

    GroupResolver() {
        for (Group group : Group.values()) {
            register(group.name(), group);
            if (group.name().startsWith("GROUP_")) {
                register(StringUtils.substringAfter(group.name(), "GROUP_"), group);
            }
        }

        register("LAST_32", Group.ROUND_OF_THIRTY_TWO);
        register("ROUND_OF_32", Group.ROUND_OF_THIRTY_TWO);
        register("LAST_16", Group.ROUND_OF_SIXTEEN);
        register("ROUND_OF_16", Group.ROUND_OF_SIXTEEN);
        register("QUARTER_FINALS", Group.QUARTER_FINAL);
        register("SEMI_FINALS", Group.SEMI_FINAL);
        register("THIRD_PLACE", Group.GAME_FOR_THIRD);

        for (Properties translations : TranslationLookup.loadAllLanguages("classpath*:msgs/TranslationMessages_*.properties")) {
            for (Group group : Group.values()) {
                String translated = translations.getProperty(group.getTitleMsgKey());
                if (translated != null) {
                    register(translated, group);
                }
            }
        }
    }

    private void register(String name, Group group) {
        String normalized = TranslationLookup.normalize(name);
        if (!normalized.isEmpty()) {
            groupsByName.putIfAbsent(normalized, group);
        }
    }

    /**
     * @return the matching group or null, if the group name is unknown
     */
    Group resolve(String groupName) {
        if (StringUtils.isBlank(groupName)) {
            return null;
        }
        return groupsByName.get(TranslationLookup.normalize(groupName));
    }
}
