package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 24 Stunden
    private static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60;

    private final Optional<H2ConsoleProperties> h2ConsoleProperties;

    public SecurityConfig(Optional<H2ConsoleProperties> h2ConsoleProperties) {
        this.h2ConsoleProperties = h2ConsoleProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository persistentTokenRepository, HandlerMappingIntrospector introspector) throws Exception {
        final MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(antMatchers("/actuator/**", "/blueimpgallery/**", "/lightbox/**",
                         "/fonts/**", "/login/**", "/logout", "/console/*", "/registration")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/user/**")).hasAnyAuthority(FredBetPermission.PERM_USER_ADMINISTRATION)
                .requestMatchers(mvcMatcherBuilder.pattern("/admin/**"), mvcMatcherBuilder.pattern("/administration/**")).hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION)
                .anyRequest().authenticated()
        );
        http.rememberMe(remember -> remember.tokenRepository(persistentTokenRepository).tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS));
        http.formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/matches/upcoming")
                .failureUrl("/login?error=true")
        );
        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me"));

        // disable cache control to allow usage of ETAG headers (no image reload if the image has not been changed)
        http.headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable));
        final MvcRequestMatcher editInfoRequestMatcher = mvcMatcherBuilder.pattern("/info/editinfo");
        if (h2ConsoleProperties.isPresent() && h2ConsoleProperties.get().getEnabled()) {
            http.csrf(csrf -> csrf.ignoringRequestMatchers(editInfoRequestMatcher, PathRequest.toH2Console()));

            // this is for the embedded h2 console
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        } else {
            http.csrf(csrf -> csrf.ignoringRequestMatchers(editInfoRequestMatcher));
        }

        return http.build();
    }

    private RequestMatcher[] antMatchers(String... patterns) {
        List<? extends RequestMatcher> matchers = Arrays.stream(patterns).map(AntPathRequestMatcher::antMatcher).toList();
        return matchers.toArray(new RequestMatcher[0]);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        jdbcTokenRepositoryImpl.setDataSource(dataSource);
        return jdbcTokenRepositoryImpl;
    }

}
