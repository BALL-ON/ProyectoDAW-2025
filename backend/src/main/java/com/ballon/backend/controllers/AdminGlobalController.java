package com.ballon.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.AdminCentroRequestDTO;
import com.ballon.backend.dtos.AdminCentroResponseDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.UsuarioRepository;
import com.ballon.backend.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminGlobalController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Endpoint para crear un director de centro (Admin_centro)
     * @param dto
     * @return
     */
    @PostMapping("/director")
    public ResponseEntity<UsuarioResponseDTO> registrarDirectorCentro(@Valid @RequestBody AdminCentroRequestDTO dto) {
       
        UsuarioResponseDTO nuevoAdmin = usuarioService.crearAdminCentro(dto);
        return ResponseEntity.ok(nuevoAdmin);
    }
    
    /**
     * Endpoint para recibir la lista de todos los admin_centro filtrado por paginacion (muestra 5 por pagina)
     * @return
     */
    @GetMapping("/directores")
    public ResponseEntity<Page<AdminCentroResponseDTO>> listarDirectores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Usuario> paginaUsuarios = usuarioRepository.buscarDirectoresPaginadosYFiltrados(nombre, email, pageable);
        
        Page<AdminCentroResponseDTO> paginaDTOs = paginaUsuarios.map(u -> {
            AdminCentroResponseDTO.AdminCentroResponseDTOBuilder builder = AdminCentroResponseDTO.builder()
                .idUsuario(u.getIdUsuario())
                .nombre(u.getNombre())
                .apellidos(u.getApellidos())
                .email(u.getEmail())
                .telefono(u.getTelefono())
                .bloqueadoHasta(u.getBloqueadoHasta());
            
            if (u.getPolideportivoAsignado() != null) {
                builder.idPolideportivo(u.getPolideportivoAsignado().getIdPolideportivo());
                builder.nombrePolideportivo(u.getPolideportivoAsignado().getNombre());
            }
            
            return builder.build();
        });

        return ResponseEntity.ok(paginaDTOs);
    }
    
    /**
     * Endpoint para activar / bloquear un admin_centro
     * @param id
     * @param suspender
     * @return
     */
    @PutMapping("/director/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstadoDirector(@PathVariable Long id, @RequestParam boolean suspender) {
        UsuarioResponseDTO adminActualizado = usuarioService.cambiarEstadoDirector(id, suspender);
        return ResponseEntity.ok(adminActualizado);
    }
}
