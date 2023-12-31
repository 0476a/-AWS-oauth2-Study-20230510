package com.study.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.study.oauth2.security.OAuth2SuccessHandler;
import com.study.oauth2.security.jwt.JwtAuthenticationEntryPoint;
import com.study.oauth2.security.jwt.JwtAuthenticationFilter;
import com.study.oauth2.security.jwt.JwtTokenProvider;
import com.study.oauth2.service.AuthService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthService authService;
	private final OAuth2SuccessHandler auth2SuccessHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable();
		http.formLogin().disable();
		http.cors();
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests()
			.antMatchers("/auth/**", "/image/**", "/mail/**")
			.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.and()
			.oauth2Login()
			.loginPage("http://localhost:3000/auth/login")
			.successHandler(auth2SuccessHandler)
			.userInfoEndpoint()
			.userService(authService);
	}
}
