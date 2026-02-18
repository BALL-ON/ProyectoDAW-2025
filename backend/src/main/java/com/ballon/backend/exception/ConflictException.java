package com.ballon.backend.exception;

public class ConflictException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public ConflictException(String mensaje) {
		super(mensaje);
	}
}
