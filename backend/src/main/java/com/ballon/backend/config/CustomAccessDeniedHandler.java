package com.ballon.backend.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		response.resetBuffer();
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String body = String.format("""
				{
				"status": 401,
				"error": "FORBIDDEN",
				"message": "%s",
				"path": "%s",
				"timestamp": "%s"
				}
				""", accessDeniedException.getMessage(), request.getRequestURI(), java.time.LocalDateTime.now());
		response.getWriter().write(body);
		response.flushBuffer();
	}
}
 
