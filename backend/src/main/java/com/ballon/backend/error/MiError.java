package com.ballon.backend.error;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MiError {

	private HttpStatus estado;
	private LocalDateTime fecha;
	private String mensaje;
	private String ipCliente;
	private String path;
	
}
