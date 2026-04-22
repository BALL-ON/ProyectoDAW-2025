package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ballon.backend.models.MensajeContacto;
import com.ballon.backend.services.MensajeContactoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contacto")
@RequiredArgsConstructor
public class MensajeContactoController {

    private final MensajeContactoService contactoService;

    // POST público: cualquiera puede enviar un mensaje
    @PostMapping
    public ResponseEntity<MensajeContacto> enviar(@RequestBody MensajeContacto mensaje) {
        return new ResponseEntity<>(contactoService.enviarMensaje(mensaje), HttpStatus.CREATED);
    }

    // GET Admin: Ver todos los mensajes
    @GetMapping
    public ResponseEntity<List<MensajeContacto>> listarTodos() {
        return ResponseEntity.ok(contactoService.listarTodos());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<MensajeContacto>> listarPendientes() {
        return ResponseEntity.ok(contactoService.listarPendientes());
    }

    @PatchMapping("/{id}/leido")
    public ResponseEntity<Void> marcarLeido(@PathVariable Long id) {
        contactoService.marcarComoLeido(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contactoService.eliminarMensaje(id);
        return ResponseEntity.noContent().build();
    }
}