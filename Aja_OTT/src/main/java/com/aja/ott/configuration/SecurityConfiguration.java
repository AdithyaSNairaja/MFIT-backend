package com.aja.ott.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfiguration {

	private  CustomUserDetailsService userDetailsService;
	private JwtFilter jwtFilter;

	public SecurityConfiguration(CustomUserDetailsService userDetailsService , JwtFilter jwtFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(
//				auth -> auth.requestMatchers("/feedback/save", "/feedback/getFeed", "/user/register").permitAll()
//						.anyRequest().authenticated());
//
//		return http.build();
		return http
				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Updated CORS configuration
				.csrf(csrf -> csrf.disable())  // Disable CSRF using the new syntax
				.authorizeHttpRequests(auth -> auth

						.requestMatchers("/update-orientation-user-appointment/{Id}",
								"/get-all-request-calls",
								"/save-request-call-back",
								"/get-all-orientation-users",
								"/user/get-all-hr",
								"/save-orientation-user",
								"/get-all-orientation-users",
								"/save-orientation-user-appointment",
								"/upcoming-appointment",
								"/get-all-orientation-user-appointment",
								"/get-all-orientation-users",
								"/save-referral",
								"/get-by-name",
								"/user/send-user-otp",
								"/user/verify-user-otp",
								"/user/save-user",
								"/user/verify-user-otp",
								"/update-trial-user-extend-date/{id}",
								"/update-trial-user/{id}",
								"/get-all-trial-user",
//								trail

								"/get-number-of-days-left-for-trial/{id}",

								"/save-trial-user",
								"/get-all-trial-users",
								"/get-trial-user-by-id/{id}",
								"/trial-user-delete/{id}",
								"/update-trial-user/{id}",


								"/user/update-user/{id}",
								"/get-all-trial-user",

								"/change-password",
								"/user/change-password",
								"/save-trial-user-feedback",
								"/update-trial-user-feedback/{id}",
								"/get-trail-exit",
								"/get-all-trial-user-feedbacks",
								"/user/save-user",
								"/save-trial-user",
								"/get-trial-user-by-id/{id}",
								"/user/register",
								"/getall/trial",
								"/user/login",
								"/user/send-otp",
								"/user/verify-otp",
								"/save-trial-user",
								"/user/reset-password",
								"/v3/api-docs/**",
		                        "/swagger-ui/**",
		                        "save-orientation-user-appointment",
		                        "get-orientation-user-appointment-by-id/{id}",
		                        "cancel-orientation-user-appointment/{id}",
		                        "/update-online-test-scores/{id}",
		                        "/save-orientation-user",
		                        "/update-orientation-user/{id}",
								            "/check-followup"           
						).permitAll()  // Permit these specific endpoints
						.anyRequest().authenticated()  // All other requests require authentication
				)
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session management for JWT
				)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)  // Add JWT filter
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of(
			    "http://localhost:5173"
			));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();

	}

	@Bean

	public AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

		//provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());

		provider.setPasswordEncoder(new BCryptPasswordEncoder(10));  // To Bcrypt the password in this case if we give normal password it will not work.

		provider.setUserDetailsService(userDetailsService);

		return provider;

	}

	@Bean

	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

		return config.getAuthenticationManager();

	}


}
