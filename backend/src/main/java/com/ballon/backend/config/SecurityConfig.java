package com.ballon.backend.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable())
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
	                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/reservas/**").hasRole("Admin_Centro")

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

	@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(List.of("*"));
	    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
}