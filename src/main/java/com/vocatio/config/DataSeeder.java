package com.vocatio.config;

import com.vocatio.model.Carrera;
import com.vocatio.repository.CarreraRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedCarreras(CarreraRepository carreraRepository) {
        return args -> {
            if (carreraRepository.count() > 0) {
                return; // ya hay datos, no sembrar nuevamente
            }

            List<Carrera> carreras = List.of(
                    Carrera.builder().nombre("Ingeniería de Sistemas").descripcion("Diseño y desarrollo de software y sistemas informáticos.").build(),
                    Carrera.builder().nombre("Medicina").descripcion("Formación para el diagnóstico y tratamiento de enfermedades.").build(),
                    Carrera.builder().nombre("Derecho").descripcion("Estudio del ordenamiento jurídico y su aplicación.").build(),
                    Carrera.builder().nombre("Arquitectura").descripcion("Diseño y construcción de espacios habitables.").build(),
                    Carrera.builder().nombre("Psicología").descripcion("Estudio del comportamiento y la mente humana.").build(),
                    Carrera.builder().nombre("Administración de Empresas").descripcion("Gestión de recursos y toma de decisiones en organizaciones.").build(),
                    Carrera.builder().nombre("Contabilidad").descripcion("Registro y análisis de la información financiera.").build(),
                    Carrera.builder().nombre("Ingeniería Civil").descripcion("Diseño y construcción de infraestructura.").build(),
                    Carrera.builder().nombre("Diseño Gráfico").descripcion("Comunicación visual y creación de piezas gráficas.").build(),
                    Carrera.builder().nombre("Marketing").descripcion("Estrategias de mercado y gestión de marca.").build()
            );

            carreraRepository.saveAll(carreras);
        };
    }
}

