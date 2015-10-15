package de.fred4jupiter.fredbet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import de.fred4jupiter.fredbet.repository.MongoDBPersistentTokenRepository;

@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final int REMEMBER_ME_TOKEN_VALIDITY_SECONDS = 24 * 60 * 60; // 24
																				// Stunden

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private MongoDBPersistentTokenRepository persistentTokenRepositoryMangoDelete;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/webjars/**", "/login", "/logout", "/static/**").permitAll();
		http.authorizeRequests().antMatchers("/user/changePassword").hasAnyAuthority(FredBetRole.ROLE_USER.name());
		http.authorizeRequests().antMatchers("/user/**").hasAnyAuthority(FredBetRole.ROLE_ADMIN.name());
		http.authorizeRequests().antMatchers("/manage/**").hasAnyAuthority(FredBetRole.ROLE_ADMIN.name());
		http.authorizeRequests().anyRequest().authenticated();

		http.formLogin().loginPage("/login").permitAll();
		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").permitAll();
		http.rememberMe().tokenRepository(persistentTokenRepositoryMangoDelete).tokenValiditySeconds(REMEMBER_ME_TOKEN_VALIDITY_SECONDS);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
