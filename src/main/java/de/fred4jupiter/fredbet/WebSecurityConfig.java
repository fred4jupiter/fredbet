package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 24 Stunden
    private static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60;

    private final Environment environment;

    private final DataSource dataSource;

    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(Environment environment, DataSource dataSource, UserDetailsService userDetailsService) {
        super(false);
        this.environment = environment;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // static resources
        http.authorizeRequests().antMatchers("/actuator/**", "/webjars/**", "/favicon.ico", "/blueimpgallery/**",
                "/lightbox/**", "/static/**", "/css/**", "/fonts/**", "/images/**", "/js/**").permitAll();

        http.authorizeRequests().antMatchers("/login", "/logout", "/console/*", "/registration").permitAll();
        http.authorizeRequests().antMatchers("/user/**").hasAnyAuthority(FredBetPermission.PERM_USER_ADMINISTRATION);
        http.authorizeRequests().antMatchers("/admin/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION);
        http.authorizeRequests().antMatchers("/buildinfo/**").hasAnyAuthority(FredBetPermission.PERM_SYSTEM_INFO);
        http.authorizeRequests().antMatchers("/administration/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION);

        http.authorizeRequests().anyRequest().authenticated();

        http.formLogin().loginPage("/login").defaultSuccessUrl("/matches/upcoming").permitAll();
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me").permitAll();
        http.rememberMe().tokenRepository(persistentTokenRepository()).tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS);
        http.userDetailsService(userDetailsService);
        // disable cache control to allow usage of ETAG headers (no image reload
        // if the image has not been changed)
        http.headers().cacheControl().disable();

        if (environment.acceptsProfiles(Profiles.of(FredBetProfile.DEV))) {
            // this is for the embedded h2 console
            http.headers().frameOptions().disable();

            // otherwise the H2 console will not work
            http.csrf().ignoringAntMatchers("/console/*");
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        jdbcTokenRepositoryImpl.setDataSource(dataSource);
        return jdbcTokenRepositoryImpl;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}
