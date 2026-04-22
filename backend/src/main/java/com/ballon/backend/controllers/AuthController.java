package com.ballon.backend.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.models.Usuario;
import com.ballon.backend.services.JwtService;
import com.ballon.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
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
     * @param usuario El objeto Usuario mapeado automaticamente desde el JSON que envia el frontend.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Usuario usuario) {
    	
    	usuarioService.guardarUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado correctamente.");
        
    }

    /**
     * Endpoint para el inicio de sesión.
     * @param body Un mapa que contiene el 'username' y la 'password' enviados desde el formulario de login.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username"); 
        String password = body.get("contrasena");

        try {
            //Comprueba si el usuario y la clave son correctos
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            // Si todo va bien, generamos el token 
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) { 
        	//Si las credenciales fallan, capturamos el error y devolvemos un código 401 (No autorizado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}
