package com.vocatio.repository;

import com.vocatio.model.Carrera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    // Método de la rama feature/listado-de-carreras para soportar la paginación y listado (Funcionalidad 1A).
    Page<Carrera> findAll(Pageable pageable);
    
    // NOTA: Si implementaste métodos de filtrado con convenciones de Spring Data JPA (ej. findByDuracionAnios),
    // deberían estar aquí. Si no se vieron en el conflicto, significa que se unificaron automáticamente
    // o que no se habían añadido aún.
}