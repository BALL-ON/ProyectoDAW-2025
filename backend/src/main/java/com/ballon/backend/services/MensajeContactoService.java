package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.models.MensajeContacto;
import com.ballon.backend.repositories.MensajeContactoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MensajeContactoService {
	
	private final MensajeContactoRepository mensajeContactoRepository;

    public MensajeContacto enviarMensaje(MensajeContacto mensaje) {
    	mensaje.setLeido(false); //Asegura que llega como no leído
        return mensajeContactoRepository.save(mensaje);
    }

    public List<MensajeContacto> listarTodos() {
        return mensajeContactoRepository.findAll();
    }
    
    public List<MensajeContacto> listarPendientes() {
        return mensajeContactoRepository.findAll()
                .stream()
                .filter(m -> !m.getLeido())
                .toList();
    }

    public void marcarComoLeido(Long id) {
        MensajeContacto mensaje = mensajeContactoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setLeido(true);
        mensajeContactoRepository.save(mensaje);
    }

    public void eliminarMensaje(Long id) {
        mensajeContactoRepository.deleteById(id);
    }
    
}
