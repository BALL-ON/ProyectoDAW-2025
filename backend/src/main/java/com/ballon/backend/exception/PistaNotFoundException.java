package com.ballon.backend.exception;

public class PistaNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public PistaNotFoundException(Long id) {
		super("No ha sido posible encontrar una Pista con el id " + id);
	}
}
