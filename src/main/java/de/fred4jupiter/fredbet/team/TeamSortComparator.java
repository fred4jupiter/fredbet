package de.fred4jupiter.fredbet.team;

import de.fred4jupiter.fredbet.domain.TranslationAware;
import de.fred4jupiter.fredbet.domain.entity.Team;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class TeamSortComparator implements Comparator<Team> {

    private final TranslationAware translationAware;

    public TeamSortComparator(TranslationAware translationAware) {
        this.translationAware = translationAware;
    }

    @Override
    public int compare(Team teamOne, Team teamTwo) {
        String teamTranslatedOne = teamOne.getNameTranslated(translationAware);
        String teamTranslatedTwo = teamTwo.getNameTranslated(translationAware);
        if (StringUtils.isEmpty(teamTranslatedOne)) {
            return -1;
        }
        if (StringUtils.isEmpty(teamTranslatedTwo)) {
            return 1;
        }

        return teamTranslatedOne.compareTo(teamTranslatedTwo);
    }
}
