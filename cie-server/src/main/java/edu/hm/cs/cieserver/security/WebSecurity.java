package edu.hm.cs.cieserver.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		securedEnabled = true,
		jsr250Enabled = true,
		prePostEnabled = true
)
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private JWTAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	public JWTAuthenticationFilter jwtAuthenticationFilter() {
		return new JWTAuthenticationFilter();
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.csrf()
				.disable()
				.exceptionHandling()
				.authenticationEntryPoint(unauthorizedHandler)
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/",
						"/favicon.ico",
						"/**/*.png",
						"/**/*.gif",
						"/**/*.svg",
						"/**/*.jpg",
						"/**/*.html",
						"/**/*.css",
						"/**/*.js")
				.permitAll()
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/api/util/**").permitAll()

				.antMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/courses/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/courses/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/courses/**").hasAuthority("ROLE_ADMIN")

				.antMatchers(HttpMethod.GET, "/api/locations/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/locations/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/locations/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/locations/**").hasAuthority("ROLE_ADMIN")

				.antMatchers(HttpMethod.GET, "/api/departments/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/departments/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/departments/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/departments/**").hasAuthority("ROLE_ADMIN")

				.antMatchers(HttpMethod.GET, "/api/lecturers/**").permitAll()
				.antMatchers(HttpMethod.POST, "/api/lecturers/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/lecturers/**").hasAuthority("ROLE_ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/lecturers/**").hasAuthority("ROLE_ADMIN")

				.antMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
				.anyRequest().authenticated();

		// Add custom JWT security filter
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

}