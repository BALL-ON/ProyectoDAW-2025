package com.ballon.backend.exception;

public class UsuarioNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public UsuarioNotFoundException(String email) {
		super("No ha sido posible encontrar un Usuario con email " + email);
	}
}
