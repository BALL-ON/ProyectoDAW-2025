package com.ballon.backend.exception;

import com.ballon.backend.models.Polideportivo;

public class PolideportivoDuplicatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public PolideportivoDuplicatedException(Polideportivo poli) {
		super("Ya existe un polideportivo con el nombre: " + poli.getNombre());
	}
}
