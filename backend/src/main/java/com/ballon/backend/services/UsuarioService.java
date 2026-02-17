package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
    	
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("¡Error! Ya existe un usuario registrado con el email: " + usuario.getEmail());
        }

        // Añadir cifrado de contraseña en la fase de Seguridad
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

}
