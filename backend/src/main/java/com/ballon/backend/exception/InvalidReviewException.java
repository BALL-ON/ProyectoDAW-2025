package com.ballon.backend.exception;

public class InvalidReviewException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public InvalidReviewException(String mensaje) {
		super(mensaje);
	}
}
