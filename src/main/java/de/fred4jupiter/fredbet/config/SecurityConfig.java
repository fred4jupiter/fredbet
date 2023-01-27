package de.fred4jupiter.fredbet.config;

import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 24 Stunden
    private static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60;

    private final Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository persistentTokenRepository) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                .requestMatchers("/actuator/**", "/webjars/**", "**/favicon.ico", "/blueimpgallery/**",
                        "/lightbox/**", "/css/**", "/fonts/**", "/images/**", "/js/**", "/login", "/logout", "/console/*", "/registration").permitAll()
                .requestMatchers("/user/**").hasAnyAuthority(FredBetPermission.PERM_USER_ADMINISTRATION)
                .requestMatchers("/admin/**", "/administration/**", "/h2/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION)
                .requestMatchers("/buildinfo/**").hasAnyAuthority(FredBetPermission.PERM_SYSTEM_INFO)
                .anyRequest().authenticated()
        );
        http.rememberMe((remember) -> remember.tokenRepository(persistentTokenRepository).tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS));
        http.formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/matches/upcoming")
                .failureUrl("/login?error=true")
        );
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("JSESSIONID", "remember-me");

        // disable cache control to allow usage of ETAG headers (no image reload if the image has not been changed)
        http.headers().cacheControl().disable();
        http.csrf().ignoringRequestMatchers("/info/editinfo");
        if (environment.acceptsProfiles(Profiles.of(FredBetProfile.DEV))) {
            // this is for the embedded h2 console
            http.headers().frameOptions().disable();

            // otherwise the H2 console will not work
            http.csrf().ignoringRequestMatchers("/h2/*");
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
