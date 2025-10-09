package com.vocatio.config;

import com.vocatio.model.Carrera;
import com.vocatio.model.Testimonio; // Importación necesaria de tu rama
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.TestimonioRepository; // Repositorio necesario de tu rama
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration // Estructura de develop
public class DataSeeder {

    private final TestimonioRepository testimonioRepository; // Nuevo campo inyectado

    public DataSeeder(TestimonioRepository testimonioRepository) { // Nuevo constructor inyectando tu Repositorio
        this.testimonioRepository = testimonioRepository;
    }

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedCarreras(CarreraRepository carreraRepository) { // Método de develop
        return args -> {
            // 1. SIEMBRA DE CARRERAS (Lógica de develop mejorada)
            if (carreraRepository.count() == 0) { // Cambiamos a '== 0' para más claridad
                List<Carrera> carreras = List.of(
                        // Se mantiene el conjunto grande de carreras de develop
                        Carrera.builder().nombre("Ingeniería de Sistemas").descripcion("Diseño y desarrollo de software y sistemas informáticos.").duracionAnios(5).modalidad("Presencial").rangoSalarioPromedio("Alto").perfilRiasec("Investigador").build(),
                        Carrera.builder().nombre("Medicina").descripcion("Formación para el diagnóstico y tratamiento de enfermedades.").duracionAnios(6).modalidad("Presencial").rangoSalarioPromedio("Alto").perfilRiasec("Social").build(),
                        Carrera.builder().nombre("Derecho").descripcion("Estudio del ordenamiento jurídico y su aplicación.").duracionAnios(5).modalidad("Presencial").rangoSalarioPromedio("Medio-Alto").perfilRiasec("Emprendedor").build(),
                        Carrera.builder().nombre("Arquitectura").descripcion("Diseño y construcción de espacios habitables.").duracionAnios(5).modalidad("Híbrido").rangoSalarioPromedio("Medio").perfilRiasec("Artístico").build(),
                        Carrera.builder().nombre("Psicología").descripcion("Estudio del comportamiento y la mente humana.").duracionAnios(5).modalidad("Presencial").rangoSalarioPromedio("Medio").perfilRiasec("Social").build(),
                        Carrera.builder().nombre("Administración de Empresas").descripcion("Gestión de recursos y toma de decisiones en organizaciones.").duracionAnios(4).modalidad("Virtual").rangoSalarioPromedio("Alto").perfilRiasec("Emprendedor").build(),
                        Carrera.builder().nombre("Contabilidad").descripcion("Registro y análisis de la información financiera.").duracionAnios(4).modalidad("Virtual").rangoSalarioPromedio("Medio").perfilRiasec("Convencional").build(),
                        Carrera.builder().nombre("Ingeniería Civil").descripcion("Diseño y construcción de infraestructura.").duracionAnios(5).modalidad("Presencial").rangoSalarioPromedio("Medio-Alto").perfilRiasec("Realista").build(),
                        Carrera.builder().nombre("Diseño Gráfico").descripcion("Comunicación visual y creación de piezas gráficas.").duracionAnios(3).modalidad("Híbrido").rangoSalarioPromedio("Bajo-Medio").perfilRiasec("Artístico").build(),
                        Carrera.builder().nombre("Marketing").descripcion("Estrategias de mercado y gestión de marca.").duracionAnios(4).modalidad("Virtual").rangoSalarioPromedio("Medio-Alto").perfilRiasec("Emprendedor").build()
                );
                carreraRepository.saveAll(carreras);
            }

            // 2. SIEMBRA DE TESTIMONIOS (Lógica de tu rama)
            if (testimonioRepository.count() == 0) {
                List<Carrera> allCarreras = carreraRepository.findAll();
                if (!allCarreras.isEmpty()) {
                    // Mapeo seguro de carreras basado en el listado de arriba
                    Carrera sis = allCarreras.stream().filter(c -> c.getNombre().contains("Sistemas")).findFirst().orElse(allCarreras.get(0));
                    Carrera adm = allCarreras.stream().filter(c -> c.getNombre().contains("Administración")).findFirst().orElse(allCarreras.get(0));

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