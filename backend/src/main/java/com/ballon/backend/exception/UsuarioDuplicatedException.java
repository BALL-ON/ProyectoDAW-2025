package com.ballon.backend.exception;

import com.ballon.backend.dtos.UsuarioRequestDTO;

public class UsuarioDuplicatedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -538802568232785412L;

	public UsuarioDuplicatedException(UsuarioRequestDTO usuarioRequest) {
		super("¡Error! Ya existe un usuario registrado con el email: " + usuarioRequest.getEmail());
	}
}
