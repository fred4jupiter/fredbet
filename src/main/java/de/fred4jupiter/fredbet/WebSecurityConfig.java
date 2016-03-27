package de.fred4jupiter.fredbet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import de.fred4jupiter.fredbet.security.FredBetPermission;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// 24 Stunden
	private static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private Environment environment;

	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/webjars/**", "/login", "/logout", "/static/**", "/console/*").permitAll();
		http.authorizeRequests().antMatchers("/user/**").hasAnyAuthority(FredBetPermission.PERM_USER_ADMINISTRATION);
		http.authorizeRequests().antMatchers("/admin/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION);
		http.authorizeRequests().antMatchers("/administration/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION);
		// Spring Boot Actuator
		http.authorizeRequests().antMatchers("/manage/**").hasAnyAuthority(FredBetPermission.PERM_ADMINISTRATION);
		http.authorizeRequests().anyRequest().authenticated();

		http.formLogin().loginPage("/login").defaultSuccessUrl("/matches/upcoming").permitAll();
		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").permitAll();
		http.rememberMe().tokenRepository(persistentTokenRepository()).tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS);

		http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());

		if (environment.acceptsProfiles(FredBetProfile.DEV)) {
			// this is for the embedded h2 console
			http.csrf().disable();
			http.headers().frameOptions().disable();
		}
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
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
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
		return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}

}
