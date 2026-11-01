package de.fred4jupiter.fredbet.info;

import de.fred4jupiter.fredbet.domain.entity.InfoContent;
import de.fred4jupiter.fredbet.domain.entity.InfoPK;
import de.fred4jupiter.fredbet.web.info.InfoType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class InfoService {

    private final InfoRepository infoRepository;

    public InfoService(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    public InfoContent saveInfoContent(InfoType infoType, String content, Locale locale) {
        InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(Locale.ROOT), locale.getLanguage());
        Optional<InfoContent> foundInfoOpt = infoRepository.findById(infoPK);
        if (foundInfoOpt.isPresent()) {
            InfoContent infoContent = foundInfoOpt.get();
            infoContent.setContent(content);
            return infoRepository.save(infoContent);
        }

        InfoContent infoContent = new InfoContent(infoPK, content);
        return infoRepository.save(infoContent);
    }

    public void saveInfoContentIfNotPresent(InfoType infoType, String content, String language) {
        InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(Locale.ROOT), language);
        Optional<InfoContent> foundInfoOpt = infoRepository.findById(infoPK);
        if (foundInfoOpt.isEmpty()) {
            InfoContent infoContent = new InfoContent(infoPK, content);
            infoRepository.save(infoContent);
        }
    }

    public InfoContent findBy(InfoType infoType, Locale locale) {
        InfoPK infoPK = new InfoPK(infoType.name().toLowerCase(Locale.ROOT), locale.getLanguage());
        Optional<InfoContent> foundInfoOpt = infoRepository.findById(infoPK);
        return foundInfoOpt.orElseGet(() -> saveInfoContent(infoType, "", locale));
    }
}
