// ...existing code...
package com.vocatio.config;

import com.vocatio.model.Carrera;
import com.vocatio.model.Testimonio;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.TestimonioRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataSeeder {

    private final TestimonioRepository testimonioRepository;

    public DataSeeder(TestimonioRepository testimonioRepository) {
        this.testimonioRepository = testimonioRepository;
    }

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedData(CarreraRepository carreraRepository) {
        return args -> {
            if (carreraRepository.count() == 0) {
                List<Carrera> carreras = List.of(
                        Carrera.builder()
                                .nombre("Ingeniería de Sistemas")
                                .descripcion("Diseño y desarrollo de software y sistemas informáticos.")
                                .duracionAnios(5)
                                .modalidad("Presencial")
                                .rangoSalarioPromedio("Alto")
                                .perfilRiasec("Investigador")
                                .build(),
                        Carrera.builder()
                                .nombre("Medicina")
                                .descripcion("Formación para el diagnóstico y tratamiento de enfermedades.")
                                .duracionAnios(6)
                                .modalidad("Presencial")
                                .rangoSalarioPromedio("Alto")
                                .perfilRiasec("Social")
                                .build(),
                        Carrera.builder()
                                .nombre("Derecho")
                                .descripcion("Estudio del ordenamiento jurídico y su aplicación.")
                                .duracionAnios(5)
                                .modalidad("Presencial")
                                .rangoSalarioPromedio("Medio-Alto")
                                .perfilRiasec("Emprendedor")
                                .build(),
                        Carrera.builder()
                                .nombre("Arquitectura")
                                .descripcion("Diseño y construcción de espacios habitables.")
                                .duracionAnios(5)
                                .modalidad("Híbrido")
                                .rangoSalarioPromedio("Medio")
                                .perfilRiasec("Artístico")
                                .build(),
                        Carrera.builder()
                                .nombre("Psicología")
                                .descripcion("Ciencia del comportamiento humano.")
                                .duracionAnios(5)
                                .modalidad("Presencial")
                                .rangoSalarioPromedio("Medio")
                                .perfilRiasec("Social")
                                .build(),
                        Carrera.builder()
                                .nombre("Administración de Empresas")
                                .descripcion("Gestión de organizaciones.")
                                .duracionAnios(4)
                                .modalidad("Virtual")
                                .rangoSalarioPromedio("Alto")
                                .perfilRiasec("Emprendedor")
                                .build(),
                        Carrera.builder()
                                .nombre("Contabilidad")
                                .descripcion("Registro y análisis de la información financiera.")
                                .duracionAnios(4)
                                .modalidad("Virtual")
                                .rangoSalarioPromedio("Medio")
                                .perfilRiasec("Convencional")
                                .build(),
                        Carrera.builder()
                                .nombre("Ingeniería Civil")
                                .descripcion("Diseño y construcción de infraestructura.")
                                .duracionAnios(5)
                                .modalidad("Presencial")
                                .rangoSalarioPromedio("Medio-Alto")
                                .perfilRiasec("Realista")
                                .build(),
                        Carrera.builder()
                                .nombre("Diseño Gráfico")
                                .descripcion("Comunicación visual.")
                                .duracionAnios(3)
                                .modalidad("Híbrido")
                                .rangoSalarioPromedio("Bajo-Medio")
                                .perfilRiasec("Artístico")
                                .build(),
                        Carrera.builder()
                                .nombre("Marketing")
                                .descripcion("Estrategias comerciales.")
                                .duracionAnios(4)
                                .modalidad("Virtual")
                                .rangoSalarioPromedio("Medio-Alto")
                                .perfilRiasec("Emprendedor")
                                .build()
                );
                carreraRepository.saveAll(carreras);
            }

            if (testimonioRepository.count() == 0) {
                List<Carrera> allCarreras = carreraRepository.findAll();
                if (!allCarreras.isEmpty()) {
                    Carrera sis = allCarreras.stream()
                            .filter(c -> c.getNombre().contains("Sistemas"))
                            .findFirst()
                            .orElse(allCarreras.get(0));
                    Carrera adm = allCarreras.stream()
                            .filter(c -> c.getNombre().contains("Administración"))
                            .findFirst()
                            .orElse(allCarreras.get(0));

                    List<Testimonio> testimonios = List.of(
                            new Testimonio("Excelente malla y profesores muy capacitados.", "Ana Pérez", sis),
                            new Testimonio("Me ayudó a conseguir mi primer trabajo en tech.", "Luis Gómez", sis),
                            new Testimonio("La modalidad virtual facilita estudiar y trabajar.", "María López", adm)
                    );
                    testimonioRepository.saveAll(testimonios);
                }
            }
        };
    }
}
// ...existing code...