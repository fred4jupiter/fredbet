package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.web.ChangePasswordFirstLoginInterceptor;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final WebSecurityUtil webSecurityUtil;

    public MvcConfig(WebSecurityUtil webSecurityUtil) {
        this.webSecurityUtil = webSecurityUtil;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/error").setViewName("error");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(changePasswordFirstLoginInterceptor());

        // registry.addInterceptor(new ExecutionTimeInterceptor());

        // for logging request header
        // registry.addInterceptor(new HeaderLogHandlerInterceptor());
    }

    @Bean
    public ChangePasswordFirstLoginInterceptor changePasswordFirstLoginInterceptor() {
        return new ChangePasswordFirstLoginInterceptor(webSecurityUtil);
    }

    @Bean
    public LocaleResolver localeResolver(FredbetProperties fredbetProperties) {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        Locale locale = StringUtils.isNotBlank(fredbetProperties.defaultLanguage()) ? Locale.of(fredbetProperties.defaultLanguage()) : Locale.getDefault();
        sessionLocaleResolver.setDefaultLocale(locale);
        return sessionLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames("classpath:org/springframework/security/messages", "classpath:/TranslationMessages", "classpath:/ValidationMessages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }
}
