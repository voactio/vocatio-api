package com.vocatio.service;

import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    public CarreraDetailResponse obtenerDetalleCarrera(Long carreraId) {
        Carrera c = carreraRepository.findById(carreraId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));
        return mapToDetail(c);
    }

    public List<CarreraDetailResponse> filtrar(String area, Integer duracion, String modalidad) {
        Specification<Carrera> spec = Specification.where(null);

        if (area != null && !area.isBlank()) {
            final String like = "%" + area.trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("perfilRiasec")), like),    // área de interés por perfil
                    cb.like(cb.lower(root.get("nombre")), like),          // fallback por nombre
                    cb.like(cb.lower(root.get("descripcion")), like)      // fallback por descripción
            ));
        }

        if (duracion != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("duracionAnios"), duracion));
        }

        if (modalidad != null && !modalidad.isBlank()) {
            final String mod = modalidad.trim().toLowerCase();
            spec = spec.and((root, q, cb) -> cb.equal(cb.lower(root.get("modalidad")), mod));
        }

        return carreraRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "nombre"))
                .stream()
                .map(this::mapToDetail)
                .toList();
    }

    private CarreraDetailResponse mapToDetail(Carrera c) {
        return new CarreraDetailResponse(
                c.getId(),
                c.getNombre(),
                c.getDescripcion(),
                c.getDuracionAnios(),
                c.getModalidad(),
                c.getRangoSalarioPromedio(),
                c.getCreadoEn(),
                c.getActualizadoEn()
        );
    }
}
