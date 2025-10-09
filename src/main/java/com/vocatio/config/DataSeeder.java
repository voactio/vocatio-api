package com.vocatio.config;

import com.vocatio.model.Carrera;
import com.vocatio.model.Testimonio;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.TestimonioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final CarreraRepository carreraRepository;
    private final TestimonioRepository testimonioRepository;

    public DataSeeder(CarreraRepository carreraRepository, TestimonioRepository testimonioRepository) {
        this.carreraRepository = carreraRepository;
        this.testimonioRepository = testimonioRepository;
    }

    @Override
    public void run(String... args) {
        if (carreraRepository.count() == 0) {
            List<Carrera> carreras = List.of(
                    new Carrera("Ingeniería de Sistemas", "Diseño y desarrollo de software", 5, "Presencial"),
                    new Carrera("Administración", "Gestión de organizaciones", 4, "Virtual"),
                    new Carrera("Diseño Gráfico", "Comunicación visual", 3, "Híbrido"),
                    new Carrera("Psicología", "Ciencia del comportamiento humano", 5, "Presencial"),
                    new Carrera("Marketing", "Estrategias comerciales", 4, "Virtual")
            );
            carreraRepository.saveAll(carreras);
        }

        // Seed de testimonios si aún no existen
        if (testimonioRepository.count() == 0) {
            List<Carrera> allCarreras = carreraRepository.findAll();
            if (!allCarreras.isEmpty()) {
                Carrera sis = allCarreras.get(0);
                Carrera adm = allCarreras.size() > 1 ? allCarreras.get(1) : allCarreras.get(0);

                List<Testimonio> testimonios = List.of(
                        new Testimonio("Excelente malla y profesores muy capacitados.", "Ana Pérez", sis),
                        new Testimonio("Me ayudó a conseguir mi primer trabajo en tech.", "Luis Gómez", sis),
                        new Testimonio("La modalidad virtual facilita estudiar y trabajar.", "María López", adm)
                );
                testimonioRepository.saveAll(testimonios);
            }
        }
    }
}

