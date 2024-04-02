package de.fred4jupiter.fredbet.data;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public record StaticResourceLoader(@Value("classpath:/content/rules_de.txt") Resource rules,
                                   @Value("classpath:/static/images/profile_demo_image.jpg") Resource demoImage) {

    private static final Logger LOG = LoggerFactory.getLogger(StaticResourceLoader.class);

    public byte[] loadDemoUserProfileImage() {
        try (InputStream in = demoImage.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load demo image from classpath. " + e.getMessage());
        }
    }

    public String loadDefaultRules() {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            IOUtils.copyLarge(rules.getInputStream(), byteOut);
            return byteOut.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
