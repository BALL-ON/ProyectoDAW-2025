package com.ballon.backend.exception;

import com.ballon.backend.models.Usuario;

public class UsuarioDuplicatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public UsuarioDuplicatedException(Usuario usuario) {
		super("¡Error! Ya existe un usuario registrado con el email: " + usuario.getEmail());
	}
}
