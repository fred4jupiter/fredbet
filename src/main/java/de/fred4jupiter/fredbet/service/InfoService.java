package de.fred4jupiter.fredbet.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.domain.InfoPK;
import de.fred4jupiter.fredbet.repository.InfoRepository;

@Service
@Transactional
public class InfoService {

	@Autowired
	private InfoRepository infoRepository;

	public Info saveInfoContent(String name, String content, Locale locale) {
		Info foundInfo = infoRepository.findOne(new InfoPK(name, locale.toString()));
		if (foundInfo == null) {
			foundInfo = new Info(name, content, locale.toString());
		}
		else {
			foundInfo.setContent(content);
		}

		return infoRepository.save(foundInfo);
	}

	public Info findBy(String name, Locale locale) {
		return infoRepository.findOne(new InfoPK(name, locale.toString()));
	}

}
