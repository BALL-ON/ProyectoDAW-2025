package com.ballon.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.Rol;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByUsername(String username);
	
	List<Usuario> findByRol(Rol rol);
	
	@Query("SELECT u FROM Usuario u WHERE u.rol = 'Admin_Centro' " +
	           "AND (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
	           "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
	    Page<Usuario> buscarDirectoresPaginadosYFiltrados(@Param("nombre") String nombre, @Param("email") String email, Pageable pageable);
	
}
