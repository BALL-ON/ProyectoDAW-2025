package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.MensajeContactoDTO;
import com.ballon.backend.mapper.MensajeContactoMapper;
import com.ballon.backend.models.MensajeContacto;
import com.ballon.backend.services.MensajeContactoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contacto")
@RequiredArgsConstructor
public class MensajeContactoController {

    private final MensajeContactoService contactoService;
    private final MensajeContactoMapper mensajeMapper;

    /**
     * POST público: cualquiera puede enviar un mensaje de contacto.
     * Las validaciones (@NotBlank, @Email, etc.) viven en el DTO.
     */
    @PostMapping
    public ResponseEntity<MensajeContactoDTO> enviar(
            @Valid @RequestBody MensajeContactoDTO dto) {
        MensajeContacto entidad = mensajeMapper.toMensajeEntity(dto);
        MensajeContacto creado = contactoService.enviarMensaje(entidad);
        return new ResponseEntity<>(
            mensajeMapper.toMensajeDto(creado),
            HttpStatus.CREATED
        );
    }

    /** GET Admin: lista todos los mensajes (leídos y pendientes). */
    @GetMapping
    public ResponseEntity<List<MensajeContactoDTO>> listarTodos() {
        return ResponseEntity.ok(
            mensajeMapper.toMensajeDtoList(contactoService.listarTodos())
        );
    }

    /** GET Admin: lista sólo los mensajes pendientes (no leídos). */
    @GetMapping("/pendientes")
    public ResponseEntity<List<MensajeContactoDTO>> listarPendientes() {
        return ResponseEntity.ok(
            mensajeMapper.toMensajeDtoList(contactoService.listarPendientes())
        );
    }

    /** PATCH Admin: marca un mensaje como leído. */
    @PatchMapping("/{id}/leido")
    public ResponseEntity<Void> marcarLeido(@PathVariable Long id) {
        contactoService.marcarComoLeido(id);
        return ResponseEntity.ok().build();
    }

    /** DELETE Admin: elimina un mensaje permanentemente. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contactoService.eliminarMensaje(id);
        return ResponseEntity.noContent().build();
    }
}
