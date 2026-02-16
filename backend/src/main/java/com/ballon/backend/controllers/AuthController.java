package com.ballon.backend.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.Rol;
import com.ballon.backend.services.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController { 

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository; 
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; 

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");

        if (usuarioRepository.findByUsername(username).isPresent()) { 
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe"); 
        }
        
        if (usuarioRepository.findByEmail(email).isPresent()) {
        	return ResponseEntity.badRequest().body("El correo ya está asociado a una cuenta existente");
        }

        Usuario usuario = new Usuario(); // Usamos vuestra entidad
        usuario.setUsername(username); 
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(password)); // Ciframos la clave 

        usuario.setRol(Rol.Usuario); // Por defecto, les ponemos rol de usuario normal
        
        usuarioRepository.save(usuario); 
        return ResponseEntity.ok("Usuario registrado correctamente en Ball-on");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username"); 
        String password = body.get("password");

        try {
            //Comprueba si el usuario y la clave son correctos
            authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            // Si todo va bien, generamos el token 
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) { 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}
