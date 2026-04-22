package com.ballon.backend.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ballon.backend.exception.UsuarioDuplicatedException;
import com.ballon.backend.exception.UsuarioNotFoundException;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.Rol;
import com.ballon.backend.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	/*
	 * Inyeccion de UsuarioRepository
	 */
	private final UsuarioRepository usuarioRepository;
	
	/*
	 * Inyeccion codificador de contraseñas
	 */
	private final PasswordEncoder passwordEncoder;

	/*
	 * Método que lista todos los usuarios existentes
	 */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /*
     * Método que guarda/regista un usuario, revisando que no existen duplicados
     * y encriptando la contraseña antes de guardarla para que sea ilegible en la bbdd
     */
    public Usuario guardarUsuario(Usuario usuario) {
    	
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new UsuarioDuplicatedException(usuario);
        }
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
       }

        String passwordEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(passwordEncriptada);
        
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.Usuario);
        }
        
        
        return usuarioRepository.save(usuario);
    }

    /*
     * Método que busca un usuario a traves de su email
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }
    
    /*
    * Método que busca un usuario a traves de su nombre de usuario
    * Lo necesitamos para el controlador de reservas.
    */
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el nombre: " + username));
    }

}

