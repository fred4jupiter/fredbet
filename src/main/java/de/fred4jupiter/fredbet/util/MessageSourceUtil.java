package de.fred4jupiter.fredbet.util;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.Country;

@Component
public class MessageSourceUtil {

	private static final Logger LOG = LoggerFactory.getLogger(MessageSourceUtil.class);

	private final MessageSource messageSource;

	@Autowired
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
}
