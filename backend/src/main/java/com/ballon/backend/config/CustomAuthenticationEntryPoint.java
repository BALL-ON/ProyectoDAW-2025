package com.ballon.backend.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.resetBuffer();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String body = String.format("""
				{
				"status": 401,
				"error": "UNAUTHORIZED",
				"message": "%s",
				"path": "%s",
				"timestamp": "%s"
				}
				""", authException.getMessage(), request.getRequestURI(), java.time.LocalDateTime.now());
		response.getWriter().write(body);
		response.flushBuffer();
	}
}
