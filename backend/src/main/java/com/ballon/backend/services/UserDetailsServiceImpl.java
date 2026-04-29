package com.ballon.backend.services;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    Usuario usuario = usuarioRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

	    // Creamos la autoridad basada en el rol de la base de datos
	    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

	    return org.springframework.security.core.userdetails.User
	            .withUsername(usuario.getEmail())
	            .password(usuario.getContrasena())
	            .authorities(List.of(authority))
	            .build();
	}

}
