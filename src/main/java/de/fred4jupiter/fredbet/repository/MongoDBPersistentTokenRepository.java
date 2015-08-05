package de.fred4jupiter.fredbet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import de.fred4jupiter.fredbet.domain.RememberMeToken;

@Repository
public class MongoDBPersistentTokenRepository implements PersistentTokenRepository {

	@Autowired
	private RememberMeTokenRepository rememberMeTokenRepository;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		RememberMeToken rememberMeToken = new RememberMeToken(token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
		rememberMeTokenRepository.save(rememberMeToken);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		RememberMeToken token = rememberMeTokenRepository.findBySeries(series);
		if (token != null) {
			RememberMeToken newToken = new RememberMeToken(token.getUsername(), series, tokenValue, new Date());
			rememberMeTokenRepository.save(newToken);
		}
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		RememberMeToken token = rememberMeTokenRepository.findBySeries(seriesId);
		if (token == null) {
			return null;
		}
		return new PersistentRememberMeToken(token.getUsername(), token.getSeries(), token.getTokenValue(), token.getLastUsed());
	}

	@Override
	public void removeUserTokens(String username) {
		List<RememberMeToken> tokenList = rememberMeTokenRepository.findByUsername(username);
		rememberMeTokenRepository.delete(tokenList);
	}

}
