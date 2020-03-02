package de.fred4jupiter.fredbet.service;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.domain.InfoPK;
import de.fred4jupiter.fredbet.repository.InfoRepository;
import de.fred4jupiter.fredbet.web.info.InfoType;

@Service
@Transactional
public class InfoService {

	@Autowired
	private InfoRepository infoRepository;

	public Info saveInfoContent(InfoType infoType, String content, Locale locale) {
		InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(), locale.getLanguage());
		Optional<Info> foundInfoOpt = infoRepository.findById(infoPK);
		if (foundInfoOpt.isPresent()) {
			Info info = foundInfoOpt.get();
			info.setContent(content);
			return infoRepository.save(info);
		}

		Info info = new Info(infoPK, content);
		return infoRepository.save(info);
	}

	public void saveInfoContentIfNotPresent(InfoType infoType, String content, String language) {
		InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(), language);
		Optional<Info> foundInfoOpt = infoRepository.findById(infoPK);
		if (!foundInfoOpt.isPresent()) {
			Info info = new Info(infoPK, content);
			infoRepository.save(info);
		}
	}

	public Info findBy(InfoType infoType, Locale locale) {
		InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(), locale.getLanguage());
		Optional<Info> foundInfoOpt = infoRepository.findById(infoPK);
		return foundInfoOpt.orElseGet(() -> saveInfoContent(infoType, "", locale));

	}

}
