package de.fred4jupiter.fredbet.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Defaults for all MockMvc requests: the pages are rendered in english and the user of
 * {@link org.springframework.security.test.context.support.WithUserDetails} is logged in.
 */
@TestConfiguration(proxyBeanMethods = false)
class MockMvcDefaults {

    @Bean
    MockMvcBuilderCustomizer englishLocaleAndTestSecurityContext() {
        return builder -> builder.defaultRequest(get("/")
            .sessionAttr(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH)
            .with(testSecurityContext()));
    }
}
