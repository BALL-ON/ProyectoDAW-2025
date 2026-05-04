package com.ballon.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Lanzada cuando se busca una pista por id y no existe.
 * Spring devolverá 404 automáticamente gracias a @ResponseStatus.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PistaNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4187293045671203958L;

    public PistaNotFoundException(Long id) {
        super("No ha sido posible encontrar una Pista con el id " + id);
    }
}
