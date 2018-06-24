package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Group;
import org.apache.commons.lang3.StringUtils;

public enum RedirectViewName {

    MATCHES("redirect:/matches"),

    OPEN_BETS("redirect:/bet/open"),

    MATCHES_UPCOMING("redirect:/matches/upcoming"),

    MATCHES_TODAY("redirect:/matches/today"),

    MATCHES_JOKER("redirect:/matches/joker");

    private static final String GROUP_PREFIX = "GROUP@";

    private String redirectViewName;

    private RedirectViewName(String redirectViewName) {
        this.redirectViewName = redirectViewName;
    }

    public String getRedirectViewName() {
        return redirectViewName;
    }

    public static RedirectViewName fromName(String name) {
        for (RedirectViewName redirectViewNames : values()) {
            if (redirectViewNames.name().equalsIgnoreCase(name)) {
                return redirectViewNames;
            }
        }

        throw new IllegalArgumentException("Could not resolve to RedirectViewName. Name " + name + " is unsupported!");
    }

    public static String resolveRedirect(String redirectViewNameString) {
        if (StringUtils.isBlank(redirectViewNameString)) {
            return RedirectViewName.MATCHES.getRedirectViewName();
        }

        if (redirectViewNameString.startsWith(GROUP_PREFIX)) {
            String group = redirectViewNameString.substring(6);
            return "redirect:/matches/group/" + group;
        }

        try {
            return RedirectViewName.fromName(redirectViewNameString).getRedirectViewName();
        } catch (IllegalArgumentException e) {
            return RedirectViewName.MATCHES.getRedirectViewName();
        }
    }

    public static String createRedirectForGroup(Group group) {
        return GROUP_PREFIX + group.name();
    }

}
