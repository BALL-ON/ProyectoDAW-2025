package com.ballon.backend.exception;

public class PolideportivoNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public PolideportivoNotFoundException(Long id) {
		super("No ha sido posible encontrar un Polideportivo con el id " + id);
	}
}
