package de.fred4jupiter.fredbet.domain;

import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;

import java.util.Locale;

public interface TranslationAware {

    String getTeamBundleEntryName(TeamBundleEntry teamBundleEntry, Locale locale);
}
