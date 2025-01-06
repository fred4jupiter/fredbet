package de.fred4jupiter.fredbet.web;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class LanguageUtil {

    public String buildUrl(String languageCode) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        return builder.replaceQueryParam("lang", languageCode).toUriString();
    }
}
