package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.h2console.autoconfigure.H2ConsoleProperties;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
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
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;
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
    public SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository persistentTokenRepository) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // see StaticResourceLocation for default static resource mappings
                .requestMatchers("/actuator/**", "/blueimpgallery/**", "/lightbox/**", "/flag-icons*/**",
                    "/club-wm-icons*/**", "/fonts/**", "/login/**", "/logout", "/registration",
                    "/webjars/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/user/**").hasAnyAuthority(FredBetPermission.PERM_USER_ADMINISTRATION)
                .requestMatchers("/admin/**", "/administration/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION)
                .anyRequest().authenticated()
            )
            .rememberMe(remember -> remember.tokenRepository(persistentTokenRepository).tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS))
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/matches/upcoming")
                .failureUrl("/login/error"))
            .logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me"))
            // disable cache control to allow usage of ETAG headers (no image reload if the image has not been changed)
            .headers(headers -> headers.cacheControl(HeadersConfigurer.CacheControlConfig::disable));

        if (h2ConsoleProperties.isPresent() && h2ConsoleProperties.get().isEnabled()) {
            // this is for the embedded h2 console
            http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
            http.csrf(csrf -> csrf.ignoringRequestMatchers("/console/**"));
        }

        return http.build();
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
