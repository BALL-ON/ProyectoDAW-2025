package com.ballon.backend.exception;

public class MensajeNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public MensajeNotFoundException(Long id) {
		super("No ha sido posible encontrar un Mensaje con el id " + id);
	}
}
