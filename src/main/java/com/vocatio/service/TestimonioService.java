// src/main/java/com/vocatio/service/TestimonioService.java
package com.vocatio.service;

import com.vocatio.dto.response.TestimonioResponse;
import com.vocatio.dto.request.CrearTestimonioRequest;
import com.vocatio.model.Testimonio;
import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.TestimonioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Service
public class TestimonioService {

    private final TestimonioRepository testimonioRepository;
    private final CarreraRepository carreraRepository;

    public TestimonioService(TestimonioRepository testimonioRepository, CarreraRepository carreraRepository) {
        this.testimonioRepository = testimonioRepository;
        this.carreraRepository = carreraRepository;
    }

    public List<TestimonioResponse> obtenerAprobadosPorCarrera(Long carreraId) {
        carreraRepository.findById(carreraId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));

        return testimonioRepository.findByCarrera_IdAndAprobadoTrueOrderByCreadoEnDesc(carreraId)
                .stream()
                .map(t -> new TestimonioResponse(
                        t.getId(),
                        t.getIdUsuario(),
                        t.getCarrera().getId(),
                        t.getTextoTestimonio(),
                        t.isAprobado(),
                        t.getCreadoEn()
                ))
                .toList();
    }
    public TestimonioResponse crearTestimonio(Long carreraId, CrearTestimonioRequest request) {
        Carrera carrera = carreraRepository.findById(carreraId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));

        Testimonio testimonio = new Testimonio();
        testimonio.setCarrera(carrera);
        testimonio.setIdUsuario(request.getIdUsuario());
        testimonio.setTextoTestimonio(request.getTextoTestimonio());
        testimonio.setAprobado(true); // Pendiente de aprobación

        Testimonio guardado = testimonioRepository.save(testimonio);

        return new TestimonioResponse(
                guardado.getId(),
                guardado.getIdUsuario(),
                guardado.getCarrera().getId(),
                guardado.getTextoTestimonio(),
                guardado.isAprobado(),
                guardado.getCreadoEn()
        );
    }
}

