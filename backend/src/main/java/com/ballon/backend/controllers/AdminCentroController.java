package com.ballon.backend.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.enums.EstadoReserva;
import com.ballon.backend.repositories.ReservaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin-centro")
@RequiredArgsConstructor
public class AdminCentroController {
	
	private final ReservaRepository reservaRepository;
	
	@PostMapping("/validar-qr/{codigoQr}")
    public ResponseEntity<?> validarReservaPorQr(@PathVariable("codigoQr") String tokenQr) {
        try {
            // Buscamos la reserva por ese código en la base de datos
            Reserva reserva = reservaRepository.findByTokenQr(tokenQr)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            // Comprobamos si la reserva es para hoy
            if (!reserva.getFechaReserva().equals(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Esta reserva no es para hoy.");
            }

            // Comprobamos si ya ha sido usada
            if (reserva.getEstadoReserva() == EstadoReserva.Disfrutada) {
                return ResponseEntity.badRequest().body("Este código QR ya ha sido escaneado previamente.");
            }

            // Si todo está bien, la marcamos como usada para que no se pueda escanear mas veces
            reserva.setEstadoReserva(EstadoReserva.Disfrutada);
            reservaRepository.save(reserva);

            // Devolvemos un mensaje de éxito con los datos para mostrarlos en pantalla
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "¡Reserva Válida!");
            respuesta.put("pista", reserva.getPista().getNombrePista());
            respuesta.put("cliente", reserva.getUsuario().getNombre());
            
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Código QR no válido o inexistente.");
        }
    }
	
	

}
