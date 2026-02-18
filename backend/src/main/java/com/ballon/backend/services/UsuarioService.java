package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.exception.UsuarioDuplicatedException;
import com.ballon.backend.exception.UsuarioNotFoundException;
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
            throw new UsuarioDuplicatedException(usuario);
        }

        // Añadir cifrado de contraseña en la fase de Seguridad
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }

}
