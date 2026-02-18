package com.ballon.backend.error;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.exception.ConflictException;
import com.ballon.backend.exception.HorarioNotFoundException;
import com.ballon.backend.exception.InvalidReviewException;
import com.ballon.backend.exception.MensajeNotFoundException;
import com.ballon.backend.exception.PistaNotFoundException;
import com.ballon.backend.exception.PolideportivoDuplicatedException;
import com.ballon.backend.exception.PolideportivoNotFoundException;
import com.ballon.backend.exception.ReservaNotFoundException;
import com.ballon.backend.exception.TipoPistaDuplicatedException;
import com.ballon.backend.exception.UsuarioDuplicatedException;
import com.ballon.backend.exception.UsuarioNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<MiError> handleBadRequest(BadRequestException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.BAD_REQUEST,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<MiError> handleConflict(ConflictException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.CONFLICT,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}
	
	@ExceptionHandler(HorarioNotFoundException.class)
	public ResponseEntity<MiError> handleHorarioNotFound(HorarioNotFoundException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.NOT_FOUND,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(InvalidReviewException.class)
	public ResponseEntity<MiError> handleInvalidReview(InvalidReviewException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.BAD_REQUEST,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(MensajeNotFoundException.class)
	public ResponseEntity<MiError> handleMensajeNotFound(MensajeNotFoundException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.NOT_FOUND,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(PistaNotFoundException.class)
	public ResponseEntity<MiError> handlePistaNotFound(PistaNotFoundException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.NOT_FOUND,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(PolideportivoDuplicatedException.class)
	public ResponseEntity<MiError> handlePolideportivoDuplicated(PolideportivoDuplicatedException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.BAD_REQUEST,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(PolideportivoNotFoundException.class)
	public ResponseEntity<MiError> handlePolideportivoNotFound(PolideportivoNotFoundException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.NOT_FOUND,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(ReservaNotFoundException.class)
	public ResponseEntity<MiError> handleReservaNotFound(ReservaNotFoundException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.NOT_FOUND,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(TipoPistaDuplicatedException.class)
	public ResponseEntity<MiError> handleTipoPistaDuplicated(TipoPistaDuplicatedException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.BAD_REQUEST,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(UsuarioDuplicatedException.class)
	public ResponseEntity<MiError> handleUsuarioDuplicated(UsuarioDuplicatedException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.BAD_REQUEST,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(UsuarioNotFoundException.class)
	public ResponseEntity<MiError> handleUsuarioNotFound(UsuarioNotFoundException ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.NOT_FOUND,
				LocalDateTime.now(),
				ex.getMessage(),
				request.getRemoteAddr(),
				request.getPathInfo());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<MiError> handleExcepcionDefault(Exception ex, HttpServletRequest request){
		MiError error = new MiError(HttpStatus.INTERNAL_SERVER_ERROR,
				LocalDateTime.now(),
				"Se ha producido un error, contacte con su Administrador",
				request.getRemoteAddr(),
				request.getServletPath());
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
