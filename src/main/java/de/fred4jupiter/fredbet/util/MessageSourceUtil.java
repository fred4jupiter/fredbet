package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.domain.TranslationAware;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageSourceUtil implements TranslationAware {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSourceUtil.class);

    private final MessageSource messageSource;

    public MessageSourceUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessageFor(String msgKey, Object... params) {
        return getMessageFor(msgKey, LocaleContextHolder.getLocale(), params);
    }

    public String getMessageFor(String msgKey, Locale locale, Object... params) {
        try {
            return messageSource.getMessage(msgKey, params, locale);
        } catch (NoSuchMessageException e) {
            LOG.error(e.getMessage());
        }

        return msgKey;
    }

    @Override
    public String getTeamBundleEntryName(TeamBundleEntry teamBundleEntry, Locale locale) {
        if (teamBundleEntry == null) {
            return null;
        }

        return getMessageFor(teamBundleEntry.getMessageKey(), locale);
    }

    public String getTeamNameOne(Match match, Locale locale) {
        return getTeamName(match.getTeamOne().getTeamBundleEntry(), match.getTeamOne().getName(), locale);
    }

    public String getTeamNameTwo(Match match, Locale locale) {
        return getTeamName(match.getTeamTwo().getTeamBundleEntry(), match.getTeamTwo().getName(), locale);
    }

    public String getTeamName(TeamBundleEntry teamBundleEntry, String teamName, Locale locale) {
        return teamBundleEntry != null ? getTeamBundleEntryName(teamBundleEntry, locale) : teamName;
    }
}
