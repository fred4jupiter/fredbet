package de.fred4jupiter.fredbet.data;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
class StaticResourceLoader {

    private final ResourceLoader resourceLoader;

    public StaticResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String loadRules(String language) {
        Resource resource = resourceLoader.getResource("classpath:/content/rules_" + language + ".txt");
        if (!resource.exists()) {
            return null;
        }
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            IOUtils.copyLarge(resource.getInputStream(), byteOut);
            return byteOut.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load default rules from static text file. " + e.getMessage());
        }
    }

}
