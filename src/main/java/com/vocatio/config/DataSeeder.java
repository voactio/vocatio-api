// java
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
                        nuevaCarrera("Ingeniería de Sistemas", "Diseño y desarrollo de software y sistemas informáticos.", 5, "Presencial", "Alto", "Investigador"),
                        nuevaCarrera("Medicina", "Formación para el diagnóstico y tratamiento de enfermedades.", 6, "Presencial", "Alto", "Social"),
                        nuevaCarrera("Derecho", "Estudio del ordenamiento jurídico y su aplicación.", 5, "Presencial", "Medio-Alto", "Emprendedor"),
                        nuevaCarrera("Arquitectura", "Diseño y construcción de espacios habitables.", 5, "Híbrido", "Medio", "Artístico"),
                        nuevaCarrera("Psicología", "Ciencia del comportamiento humano.", 5, "Presencial", "Medio", "Social"),
                        nuevaCarrera("Administración de Empresas", "Gestión de organizaciones.", 4, "Virtual", "Alto", "Emprendedor"),
                        nuevaCarrera("Contabilidad", "Registro y análisis de la información financiera.", 4, "Virtual", "Medio", "Convencional"),
                        nuevaCarrera("Ingeniería Civil", "Diseño y construcción de infraestructura.", 5, "Presencial", "Medio-Alto", "Realista"),
                        nuevaCarrera("Diseño Gráfico", "Comunicación visual.", 3, "Híbrido", "Bajo-Medio", "Artístico"),
                        nuevaCarrera("Marketing", "Estrategias comerciales.", 4, "Virtual", "Medio-Alto", "Emprendedor")
                );
                carreraRepository.saveAll(carreras);
            }

            if (testimonioRepository.count() == 0) {
                List<Carrera> allCarreras = carreraRepository.findAll();
                if (!allCarreras.isEmpty()) {
                    Carrera sis = allCarreras.stream()
                            .filter(c -> c.getNombre() != null && c.getNombre().contains("Sistemas"))
                            .findFirst()
                            .orElse(allCarreras.get(0));
                    Carrera adm = allCarreras.stream()
                            .filter(c -> c.getNombre() != null && c.getNombre().contains("Administración"))
                            .findFirst()
                            .orElse(allCarreras.get(0));

                    List<Testimonio> testimonios = List.of(
                            nuevoTestimonio("Excelente malla y profesores muy capacitados.", "ana.perez", sis),
                            nuevoTestimonio("Me ayudó a conseguir mi primer trabajo en tech.", "luis.gomez", sis),
                            nuevoTestimonio("La modalidad virtual facilita estudiar y trabajar.", "maria.lopez", adm)
                    );
                    testimonioRepository.saveAll(testimonios);
                }
            }
        };
    }

    private Carrera nuevaCarrera(String nombre, String descripcion, int duracion, String modalidad, String salario, String perfil) {
        Carrera c = new Carrera();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setDuracionAnios(duracion);
        c.setModalidad(modalidad);
        c.setRangoSalarioPromedio(salario);
        c.setPerfilRiasec(perfil);
        return c;
    }

    private Testimonio nuevoTestimonio(String texto, String idUsuario, Carrera carrera) {
        Testimonio t = new Testimonio();
        t.setTextoTestimonio(texto);
        t.setIdUsuario(idUsuario);
        t.setCarrera(carrera);
        t.setAprobado(true);
        return t;
    }
}
