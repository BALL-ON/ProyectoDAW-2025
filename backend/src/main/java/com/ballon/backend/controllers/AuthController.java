package com.ballon.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.AuthResponseDTO;
import com.ballon.backend.dtos.LoginRequestDTO;
import com.ballon.backend.dtos.UsuarioRequestDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.models.enums.Rol;
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

    
    /**
     * Endpoint para registrar nuevos usuarios
     * @param request El objeto DTO mapeado automaticamente desde el JSON que envia el frontend.
     */
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody UsuarioRequestDTO request) {
        //  Forzamos el rol por seguridad
        request.setRol(Rol.Usuario);
        
        //Guardamos y recogemos el usuario convertido a DTO
        UsuarioResponseDTO nuevoUsuario = usuarioService.guardarUsuario(request);
        
        // Devolvemos el DTO
        return ResponseEntity.ok(nuevoUsuario);
    }

    /**
     * Endpoint para el inicio de sesión.
     * @param body Un mapa que contiene el 'username' y la 'password' enviados desde el formulario de login.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
    	try {
            // Spring Security comprueba las credenciales y nos devuelve el objeto de autenticación
            var authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getContrasena())
            );

            // Extraemos el rol (Spring le añade el prefijo "ROLE_" que está en UserDetailsServiceImpl)
            String rol = authentication.getAuthorities().iterator().next().getAuthority();

            // Generamos el token
            String token = jwtService.generateToken(request.getEmail(), request.isRemember());
            
            // Devolvemos AMBAS cosas al frontend
            return ResponseEntity.ok(new AuthResponseDTO(token, rol));

        } catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}
