package de.fred4jupiter.fredbet.service;

import java.util.Locale;

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
		Info foundInfo = infoRepository.findOne(infoPK);
		if (foundInfo == null) {
			foundInfo = new Info(infoPK, content);
		} else {
			foundInfo.setContent(content);
		}

		return infoRepository.save(foundInfo);
	}
	
	public Info saveInfoContentIfNotPresent(InfoType infoType, String content, Locale locale) {
		InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(), locale.getLanguage());
		Info foundInfo = infoRepository.findOne(infoPK);
		if (foundInfo == null) {
			foundInfo = new Info(infoPK, content);
			infoRepository.save(foundInfo);
		} 
		return foundInfo;
	}

	public Info findBy(InfoType infoType, Locale locale) {
		InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(), locale.getLanguage());
		Info info = infoRepository.findOne(infoPK);
		if (info == null) {
			info = saveInfoContent(infoType, "", locale);
		}
		return info;
	}

}
