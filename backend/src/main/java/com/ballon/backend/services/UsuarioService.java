package com.ballon.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ballon.backend.dtos.AdminCentroRequestDTO;
import com.ballon.backend.dtos.CambiarPasswordDTO;
import com.ballon.backend.dtos.UsuarioRequestDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.dtos.UsuarioUpdateDTO;
import com.ballon.backend.exception.UsuarioDuplicatedException;
import com.ballon.backend.exception.UsuarioNotFoundException;
import com.ballon.backend.mapper.UsuarioMapper;
import com.ballon.backend.models.Polideportivo;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.Rol;
import com.ballon.backend.repositories.PolideportivoRepository;
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
	 * Inyeccion de PolideportivoRepository
	 */
	private final PolideportivoRepository polideportivoRepository;
	
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
    public UsuarioResponseDTO guardarUsuario(UsuarioRequestDTO usuarioRequest, MultipartFile foto) {
    	
        if (usuarioRepository.existsByEmail(usuarioRequest.getEmail())) {
            throw new UsuarioDuplicatedException(usuarioRequest);
        }
        
        try {
            Usuario usuario = usuarioMapper.toEntity(usuarioRequest);
    
            // Copiamos el email en el campo username
            usuario.setUsername(usuarioRequest.getEmail());
    
            // Encriptamos la contraseña en la entidad
            usuario.setContrasena(passwordEncoder.encode(usuarioRequest.getContrasena()));
            
            // Forzamos a que todo nuevo registro sea de tipo Usuario normal.
            usuario.setRol(Rol.Usuario);
            
            // Sacamos los bytes de la foto
            usuario.setFotoPerfil(foto.getBytes());
            
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            
            // Devolvemos la entidad traducida a ResponseDTO
            return usuarioMapper.toResponse(usuarioGuardado);
        
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la foto o guardar el usuario", e);
        }
    }
    
    /**
     * Método para registrar un adminCentro
     * @param dto
     */
    public UsuarioResponseDTO crearAdminCentro(AdminCentroRequestDTO dto) {
        
        Usuario nuevoAdmin = new Usuario();
        nuevoAdmin.setNombre(dto.getNombre());
        nuevoAdmin.setApellidos(dto.getApellidos());
        nuevoAdmin.setEmail(dto.getEmail());
        nuevoAdmin.setUsername(dto.getEmail());      
        nuevoAdmin.setTelefono(dto.getTelefono());
        nuevoAdmin.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        nuevoAdmin.setRol(Rol.Admin_Centro); 
        
        Polideportivo p = polideportivoRepository.findById(dto.getIdPolideportivo())
            .orElseThrow(() -> new RuntimeException("Polideportivo no encontrado"));
        nuevoAdmin.setPolideportivoAsignado(p);
        
        Usuario adminGuardado = usuarioRepository.save(nuevoAdmin);
        
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdUsuario(adminGuardado.getIdUsuario());
        response.setNombre(adminGuardado.getNombre());
        response.setApellidos(adminGuardado.getApellidos());
        response.setEmail(adminGuardado.getEmail());
        response.setRol(adminGuardado.getRol());
        
        return response;
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
    
    public List<UsuarioResponseDTO> listarDirectoresCentro() {

        List<Usuario> directores = usuarioRepository.findByRol(Rol.Admin_Centro);

        return directores.stream().map(admin -> {
            UsuarioResponseDTO dto = new UsuarioResponseDTO();
            dto.setIdUsuario(admin.getIdUsuario());
            dto.setNombre(admin.getNombre());
            dto.setApellidos(admin.getApellidos());
            dto.setEmail(admin.getEmail());
            dto.setTelefono(admin.getTelefono());
            dto.setRol(admin.getRol());
            dto.setPuntosPenalizacion(admin.getPuntosPenalizacion());
            dto.setBloqueadoHasta(admin.getBloqueadoHasta());

            if (admin.getPolideportivoAsignado() != null) {
                dto.setIdPolideportivoAsignado(admin.getPolideportivoAsignado().getIdPolideportivo());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * Metodo para suspender / activar a un admin_centro
     * Bloquea el usuario poniendole una fecha lejana y lo activa quitando su bloqueo.
     * @param idUsuario
     * @param suspender
     * @return
     */
	public UsuarioResponseDTO cambiarEstadoDirector(Long idUsuario, boolean suspender) {
	
	        Usuario admin = usuarioRepository.findById(idUsuario)
	            .orElseThrow(() -> new RuntimeException("Director no encontrado con ID: " + idUsuario));
	
	        if (suspender) {
	            // Si lo suspendemos le ponemos una fecha de bloqueo lejana (100 años)
	            admin.setBloqueadoHasta(LocalDateTime.now().plusYears(100));
	        } else {
	            // Si lo reactivamos le quitamos el bloqueo
	            admin.setBloqueadoHasta(null);
	        }
	
	        Usuario adminActualizado = usuarioRepository.save(admin);
	
	        UsuarioResponseDTO dto = new UsuarioResponseDTO();
	        dto.setIdUsuario(adminActualizado.getIdUsuario());
	        dto.setNombre(adminActualizado.getNombre());
	        dto.setApellidos(adminActualizado.getApellidos());
	        dto.setEmail(adminActualizado.getEmail());
	        dto.setTelefono(adminActualizado.getTelefono());
	        dto.setRol(adminActualizado.getRol());
	        dto.setPuntosPenalizacion(adminActualizado.getPuntosPenalizacion());
	        dto.setBloqueadoHasta(adminActualizado.getBloqueadoHasta());
	        
	        if (adminActualizado.getPolideportivoAsignado() != null) {
	            dto.setIdPolideportivoAsignado(adminActualizado.getPolideportivoAsignado().getIdPolideportivo());
	        }
	
	        return dto;
	    }

	public void cambiarPassword(String email, CambiarPasswordDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasenaActual(), usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        usuario.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        usuarioRepository.save(usuario);
    }

}

