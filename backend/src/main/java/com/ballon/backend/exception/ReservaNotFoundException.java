package com.ballon.backend.exception;

public class ReservaNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public ReservaNotFoundException(Long id) {
		super("No ha sido posible encontrar una Reserva con el id " + id);
	}
}
