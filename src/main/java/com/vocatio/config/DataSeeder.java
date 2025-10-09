package com.vocatio.config;

import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final CarreraRepository carreraRepository;

    public DataSeeder(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public void run(String... args) {
        if (carreraRepository.count() > 0) return;

        List<Carrera> data = List.of(
                new Carrera("Ingeniería de Sistemas", "Diseño y desarrollo de software", 5, "Presencial"),
                new Carrera("Administración", "Gestión de organizaciones", 4, "Virtual"),
                new Carrera("Diseño Gráfico", "Comunicación visual", 3, "Híbrido"),
                new Carrera("Psicología", "Ciencia del comportamiento humano", 5, "Presencial"),
                new Carrera("Marketing", "Estrategias comerciales", 4, "Virtual")
        );
        carreraRepository.saveAll(data);
    }
}
