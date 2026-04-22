package com.ballon.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ballon.backend.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtFilter;
	private final UserDetailsService userDetailsService;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
			CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
			CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
		http
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable())
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
					// NIVEL PUBLICO: Todo el mundo puede entrar aquí (incluso sin login)
	                .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
	                .requestMatchers(org.springframework.http.HttpMethod.GET, 
	                        "/api/polideportivos/**", 
	                        "/api/pistas/**", 
	                        "/api/tipos-pista/**",
	                        "/api/resenas/**",
	                        "/api/horarios/**").permitAll()
	                
	                // NIVEL USUARIO: Solo usuarios logueado (Usuarios y Admins)
	                .requestMatchers("/api/usuarios/perfil").authenticated()
	                .requestMatchers("/api/reservas/**").hasAnyRole("Admin_Centro", "Usuario")
	                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/resenas").authenticated()

	                // NIVEL ADMIN (Admin_Centro)
	                .requestMatchers("/api/admin/**").hasRole("Admin_Centro")
	                .requestMatchers("/api/usuarios").hasRole("Admin_Centro")
	                .requestMatchers("/api/contacto/**").hasRole("Admin_Centro")
	                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/polideportivos/**", "/api/pistas/**").hasRole("Admin_Centro")
	                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/polideportivos/**", "/api/pistas/**").hasRole("Admin_Centro")
	                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/polideportivos/**", "/api/pistas/**").hasRole("Admin_Centro")

	                .anyRequest().authenticated()
				).exceptionHandling(ex -> ex
					.authenticationEntryPoint(customAuthenticationEntryPoint)
					.accessDeniedHandler(customAccessDeniedHandler)
				)
				.addFilterAfter(
						jwtFilter, 
						UsernamePasswordAuthenticationFilter.class
				);
		
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	/*@Bean
	public AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
	    provider.setPasswordEncoder(passwordEncoder());

	    return provider;
	}*/

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}