package com.ballon.backend.exception;

import com.ballon.backend.models.TipoPista;

public class TipoPistaDuplicatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public TipoPistaDuplicatedException(TipoPista tipo) {
		super("El tipo de pista " + tipo.getNombreTipo()  + " ya está creada." );
	}
}
