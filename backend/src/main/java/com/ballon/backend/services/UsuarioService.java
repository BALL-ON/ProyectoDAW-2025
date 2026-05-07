package com.ballon.backend.services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ballon.backend.dtos.UsuarioRequestDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.dtos.UsuarioUpdateDTO;
import com.ballon.backend.exception.UsuarioDuplicatedException;
import com.ballon.backend.exception.UsuarioNotFoundException;
import com.ballon.backend.mapper.UsuarioMapper;
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
	 * Inyeccion del mapper de Usuario
	 */
	private final UsuarioMapper usuarioMapper;

	/*
	 * Método que lista todos los usuarios existentes
	 */
    public List<UsuarioResponseDTO> listarTodos() {
    	return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    /*
     * Método que guarda/regista un usuario, revisando que no existen duplicados
     * y encriptando la contraseña antes de guardarla para que sea ilegible en la bbdd
     */
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequest) {
    	
        if (usuarioRepository.existsByEmail(usuarioRequest.getEmail())) {
            throw new UsuarioDuplicatedException(usuarioRequest);
        }
        
        Usuario usuario = usuarioMapper.toEntity(usuarioRequest);

        // Copiamos el email en el campo username para cumplir con la base de datos (el username es el email)
        usuario.setUsername(usuarioRequest.getEmail());

        // Encriptamos la contraseña en la entidad
        usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
        
        // Si no viene rol, forzamos que sea Usuario
        if (usuarioRequest.getRol() == null) {
        	usuarioRequest.setRol(Rol.Usuario);
        }
        
        // Guardamos en Base de Datos
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        //Devolvemos la entidad traducida a ResponseDTO
        return usuarioMapper.toResponse(usuarioGuardado);
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
    */
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el nombre: " + username));
    }
    
    /*
     * Método que obtene el perfil de un usuario usando su email por método interno para buscar la entidad
     */
    public UsuarioResponseDTO buscarPerfilPorEmail(String email) {
        // Reutilizamos el método interno para buscar la entidad
        Usuario usuario = buscarPorEmail(email);
        return usuarioMapper.toResponse(usuario);
    }

    /*
     * Método que obtiene el perfil de un usuario usando su username por método interno para buscar la entidad
     */
    public UsuarioResponseDTO buscarPerfilPorUsername(String username) {
        Usuario usuario = buscarPorUsername(username);
        return usuarioMapper.toResponse(usuario);
    }
    
    /*
     * Método que actualiza datos de un usuario
     */
    public UsuarioResponseDTO actualizarPerfil(String email, UsuarioUpdateDTO updateDTO) {
        Usuario usuario = buscarPorEmail(email);
        
        usuario.setNombre(updateDTO.getNombre());
        usuario.setApellidos(updateDTO.getApellidos());
        usuario.setTelefono(updateDTO.getTelefono());
        
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuarioActualizado);
    }

}

