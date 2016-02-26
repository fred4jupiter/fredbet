package de.fred4jupiter.fredbet.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import de.fred4jupiter.fredbet.domain.RememberMeToken;

@Repository
public class DefaultPersistentTokenRepository implements PersistentTokenRepository {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultPersistentTokenRepository.class);

	@Autowired
	private RememberMeTokenRepository rememberMeTokenRepository;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		RememberMeToken rememberMeToken = new RememberMeToken(token.getUsername(), token.getSeries(), token.getTokenValue(),
				token.getDate());
		rememberMeTokenRepository.save(rememberMeToken);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		RememberMeToken token = rememberMeTokenRepository.findBySeriesAndLastUsed(series, lastUsed);
		if (token != null) {
			token.setTokenValue(tokenValue);
			rememberMeTokenRepository.save(token);
		} else {
			LOG.warn("updateToken: Could not find token with series={} and lastUsed={}", series, lastUsed);
		}
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String series) {
		List<RememberMeToken> tokenList = rememberMeTokenRepository.findBySeries(series);
		if (tokenList == null || tokenList.isEmpty()) {
			return null;
		}

		Collections.sort(tokenList, new Comparator<RememberMeToken>() {

			@Override
			public int compare(RememberMeToken o1, RememberMeToken o2) {
				return o2.getLastUsed().compareTo(o1.getLastUsed());
			}
		});

		RememberMeToken lastToken = tokenList.get(0);
		return new PersistentRememberMeToken(lastToken.getUsername(), lastToken.getSeries(), lastToken.getTokenValue(),
				lastToken.getLastUsed());
	}

	@Override
	public void removeUserTokens(String username) {
		List<RememberMeToken> tokenList = rememberMeTokenRepository.findByUsername(username);
		rememberMeTokenRepository.delete(tokenList);
	}

}
