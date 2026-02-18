package com.ballon.backend.exception;

public class BadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public BadRequestException(String mensaje) {
		super(mensaje);
	}
}
