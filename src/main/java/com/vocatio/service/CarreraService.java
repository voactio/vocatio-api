package com.vocatio.service;

import com.vocatio.dto.response.CarreraCardResponse;
import com.vocatio.dto.response.CarreraDetailResponse;
import com.vocatio.dto.response.CarreraOptionResponse;
import com.vocatio.exception.ResourceNotFoundException;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    public List<CarreraCardResponse> listarInicial(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "nombre"));
        return carreraRepository.findAll(pageable)
                .map(this::toCard)
                .getContent();
    }

    public CarreraDetailResponse obtenerDetalle(Long id) {
        Carrera c = carreraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrera no encontrada"));
        return toDetail(c);
    }

    public List<CarreraCardResponse> buscar(String nombre, String modalidad, String perfilRiasec,
                                            int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Specification<Carrera> spec = Specification.where(null);

        if (isNotBlank(nombre)) {
            spec = spec.and((root, cq, cb) ->
                    cb.like(cb.lower(root.get("nombre")), like(nombre)));
        }
        if (isNotBlank(modalidad)) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(cb.lower(root.get("modalidad")), modalidad.toLowerCase()));
        }
        if (isNotBlank(perfilRiasec)) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(cb.lower(root.get("perfilRiasec")), perfilRiasec.toLowerCase()));
        }

        return carreraRepository.findAll(spec, pageable)
                .map(this::toCard)
                .getContent();
    }

    public List<CarreraOptionResponse> listarOpciones(){
        return carreraRepository.findAll(Sort.by("nombre"))
                .stream()
                .map(c->new CarreraOptionResponse(c.getId(), c.getNombre()))
                .toList();
    }

    private CarreraCardResponse toCard(Carrera c) {
        return new CarreraCardResponse(
                c.getId(),
                nullSafe(c.getNombre()),
                shorten(nullSafe(c.getDescripcion()), 160)
        );
    }

    private CarreraDetailResponse toDetail(Carrera c) {
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

    private String nullSafe(String s) { return s == null ? "" : s; }

    private String shorten(String s, int max) {
        String flat = s.replaceAll("\\s+", " ").trim();
        if (flat.length() <= max) return flat;
        return flat.substring(0, Math.max(0, max - 1)).trim() + "…";
        }

    private Sort parseSort(String sort) {
        if (!isNotBlank(sort)) return Sort.by(Sort.Direction.ASC, "nombre");
        String[] parts = sort.split(",");
        String prop = parts.length > 0 && isNotBlank(parts[0]) ? parts[0].trim() : "nombre";
        Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, prop);
    }

    private boolean isNotBlank(String s) { return s != null && !s.trim().isEmpty(); }

    private String like(String s) { return "%" + s.toLowerCase().trim() + "%"; }
}
