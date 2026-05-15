package com.ballon.backend.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ballon.backend.dtos.AuthResponseDTO;
import com.ballon.backend.dtos.LoginRequestDTO;
import com.ballon.backend.dtos.UsuarioRequestDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.exception.UsuarioDuplicatedException;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.UsuarioRepository;
import com.ballon.backend.services.JwtService;
import com.ballon.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController { 

	/*
	 * Inyeccion de motor de Spring Security que se encarga de validar si las credenciales son correctas
	 */
    private final AuthenticationManager authManager;
    
    /*
     * Inyeccion servicio que contiene la logica de negocio
     */
    private final UsuarioService usuarioService;
    
    /*
     * Inyeccion de servicio que crea, firma y lee tokens JWT
     */
    private final JwtService jwtService; 
    
    /*
     * Inyeccion de UsuarioRepository
     */
    private final UsuarioRepository usuarioRepository; 

    
    /**
     * Endpoint para registrar nuevos usuarios
     * @param request El objeto DTO mapeado automaticamente desde el JSON que envia el frontend.
     */
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@RequestPart("usuario") UsuarioRequestDTO request, @RequestPart("foto") MultipartFile foto) {
        
        try {
        	UsuarioResponseDTO nuevoUsuario = usuarioService.guardarUsuario(request, foto);
            return ResponseEntity.ok(nuevoUsuario);

        } catch (UsuarioDuplicatedException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El email ya está registrado"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al registrar el usuario"));
        }
    }

    /**
     * Endpoint para el inicio de sesión.
     * @param body Un mapa que contiene el 'username' y la 'password' enviados desde el formulario de login.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
    	try {
            Long idPolideportivo = null; 

    		Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
                    
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("mensaje", "Su cuenta está bloqueada temporalmente. Contacte con el administrador.");
                    
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }

                if (usuario.getPolideportivoAsignado() != null) {
                    idPolideportivo = usuario.getPolideportivoAsignado().getIdPolideportivo();
                }
            }
            
            var authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
            );

            // Extraemos el rol
            String rol = authentication.getAuthorities().iterator().next().getAuthority();

            // Generamos el token
            String token = jwtService.generateToken(request.getEmail(), request.isRemember());
            
            // Devolvemos las 3 cosas al frontend
            return ResponseEntity.ok(new AuthResponseDTO(token, rol, idPolideportivo));

        } catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Credenciales inválidas"));
        }
    }
}
