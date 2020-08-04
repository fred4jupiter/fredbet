package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageSourceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSourceUtil.class);

    private final MessageSource messageSource;

    public MessageSourceUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessageFor(String msgKey, Locale locale, Object... params) {
        try {
            return messageSource.getMessage(msgKey, params, locale);
        } catch (NoSuchMessageException e) {
            LOG.error(e.getMessage());
        }

        return msgKey;
    }

    public String getCountryName(Country country, Locale locale) {
        if (country == null) {
            return null;
        }
        return getMessageFor("country." + country.getIsoCode(), locale);
    }

    public String getTeamNameOne(Match match, Locale locale) {
        return getTeamName(match.getTeamOne().getCountry(), match.getTeamOne().getName(), locale);
    }

    public String getTeamNameTwo(Match match, Locale locale) {
        return getTeamName(match.getTeamTwo().getCountry(), match.getTeamTwo().getName(), locale);
    }

    public String getTeamName(Country country, String teamName, Locale locale) {
        if (country == null) {
            return teamName;
        }
        return Validator.isNotNull(country) ? getCountryName(country, locale) : teamName;
    }
}
