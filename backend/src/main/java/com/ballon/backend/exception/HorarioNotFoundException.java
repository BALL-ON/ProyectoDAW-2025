package com.ballon.backend.exception;

public class HorarioNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public HorarioNotFoundException(Long id) {
		super("No ha sido posible encontrar un Horario con el id " + id);
	}
}
