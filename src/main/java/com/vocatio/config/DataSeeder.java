package com.vocatio.config;

import com.vocatio.model.*;
import com.vocatio.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@Profile("!test")
public class DataSeeder {

    private final TestimonioRepository testimonioRepository;
    private final CarreraRepository carreraRepository;
    private final AreaInteresRepository areaInteresRepository;
    private final TestRepository testRepository;
    private final PreguntaRepository preguntaRepository;
    private final OpcionRepository opcionRepository;

    public DataSeeder(TestimonioRepository testimonioRepository,
                      CarreraRepository carreraRepository,
                      AreaInteresRepository areaInteresRepository,
                      TestRepository testRepository,
                      PreguntaRepository preguntaRepository,
                      OpcionRepository opcionRepository) {
        this.testimonioRepository = testimonioRepository;
        this.carreraRepository = carreraRepository;
        this.areaInteresRepository = areaInteresRepository;
        this.testRepository = testRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
    }

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedData() {
        return args -> cargarDatosCompletos();
    }

    @Transactional
    public void cargarDatosCompletos() {

        // --- 1. CARRERAS ---
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

        // --- 2. TESTIMONIOS ---
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
                        nuevoTestimonio("Excelente malla y profesores muy capacitados.", UUID.fromString("a1b2c3d4-e5f6-7890-ab12-cdef34567890"), sis),
                        nuevoTestimonio("Me ayudó a conseguir mi primer trabajo en tech.", UUID.fromString("b2c3d4e5-f678-9012-ab34-cdef45678901"), sis),
                        nuevoTestimonio("La modalidad virtual facilita estudiar y trabajar.", UUID.fromString("c3d4e5f6-7890-1234-ab56-cdef56789012"), adm)
                );
                testimonioRepository.saveAll(testimonios);
            }
        }

        // --- 3. RIASEC ---
        Map<String, AreaInteres> areasMap = new HashMap<>();

        if (areaInteresRepository.count() == 0) {
            areasMap.put("Realista", crearArea("Realista", "Prefiere trabajar con objetos, máquinas y herramientas."));
            areasMap.put("Investigador", crearArea("Investigador", "Prefiere observar, investigar y resolver problemas complejos."));
            areasMap.put("Artístico", crearArea("Artístico", "Prefiere actividades creativas, libres y originales."));
            areasMap.put("Social", crearArea("Social", "Prefiere ayudar, enseñar y trabajar con personas."));
            areasMap.put("Emprendedor", crearArea("Emprendedor", "Prefiere liderar, persuadir y gestionar proyectos."));
            areasMap.put("Convencional", crearArea("Convencional", "Prefiere organizar datos, seguir reglas y el orden."));
        } else {
            areaInteresRepository.findAll().forEach(a -> areasMap.put(a.getNombre(), a));
        }

        // --- 4. TEST VOCACIONAL ---
        if (testRepository.count() == 0) {
            Test test = new Test();
            test.setTitulo("Test Vocacional Vocatio");
            test = testRepository.save(test);

            // Pregunta 1
            crearPreguntaCompleta(test, 1, "¿Qué actividad prefieres en tu tiempo libre?", List.of(
                    new OpcionData("Armar y reparar computadoras u objetos.", areasMap.get("Realista")),
                    new OpcionData("Leer artículos de ciencia o tecnología.", areasMap.get("Investigador")),
                    new OpcionData("Dibujar, pintar o editar videos.", areasMap.get("Artístico")),
                    new OpcionData("Enseñar a alguien a hacer algo nuevo.", areasMap.get("Social"))
            ));

            // Pregunta 2
            crearPreguntaCompleta(test, 2, "En un equipo de trabajo, ¿cuál es tu rol ideal?", List.of(
                    new OpcionData("El líder que convence y motiva a todos.", areasMap.get("Emprendedor")),
                    new OpcionData("El organizador que lleva la agenda y los archivos.", areasMap.get("Convencional")),
                    new OpcionData("El analista que investiga la información.", areasMap.get("Investigador")),
                    new OpcionData("El constructor que hace la maqueta o prototipo.", areasMap.get("Realista"))
            ));

            // Pregunta 3
            crearPreguntaCompleta(test, 3, "¿Qué materia te gustaba más en el colegio?", List.of(
                    new OpcionData("Matemáticas, Física o Química.", areasMap.get("Investigador")),
                    new OpcionData("Arte, Música o Literatura.", areasMap.get("Artístico")),
                    new OpcionData("Ciencias Sociales o Historia.", areasMap.get("Social")),
                    new OpcionData("Economía o Gestión Empresarial.", areasMap.get("Emprendedor"))
            ));

            // Pregunta 4
            crearPreguntaCompleta(test, 4, "¿Cómo prefieres resolver un problema?", List.of(
                    new OpcionData("Siguiendo un manual paso a paso.", areasMap.get("Convencional")),
                    new OpcionData("Inventando una solución creativa y única.", areasMap.get("Artístico")),
                    new OpcionData("Debatiendo con otros para llegar a un acuerdo.", areasMap.get("Emprendedor")),
                    new OpcionData("Probando y experimentando físicamente.", areasMap.get("Realista"))
            ));

            // Pregunta 5
            crearPreguntaCompleta(test, 5, "¿Qué ambiente de trabajo prefieres?", List.of(
                    new OpcionData("Un laboratorio o centro de investigación.", areasMap.get("Investigador")),
                    new OpcionData("Una oficina organizada y tranquila.", areasMap.get("Convencional")),
                    new OpcionData("Un hospital o escuela ayudando gente.", areasMap.get("Social")),
                    new OpcionData("Un estudio de arte o agencia de publicidad.", areasMap.get("Artístico"))
            ));
        }
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

    private Testimonio nuevoTestimonio(String texto, UUID idUsuario, Carrera carrera) {
        Testimonio t = new Testimonio();
        t.setTextoTestimonio(texto);
        t.setIdUsuario(idUsuario);
        t.setCarrera(carrera);
        t.setAprobado(true);
        return t;
    }

    private AreaInteres crearArea(String nombre, String descripcion) {
        AreaInteres area = new AreaInteres();
        area.setNombre(nombre);
        area.setDescripcion(descripcion);
        return areaInteresRepository.save(area);
    }

    private void crearPreguntaCompleta(Test test, int orden, String texto, List<OpcionData> opcionesData) {
        Pregunta p = new Pregunta();
        p.setTest(test);
        p.setOrden(orden);
        p.setTextoPregunta(texto);
        p.setOpciones(new ArrayList<>());

        for (OpcionData data : opcionesData) {
            Opcion op = new Opcion();
            op.setTextoOpcion(data.texto);
            op.setAreaInteres(data.area);
            op.setPregunta(p);
            p.getOpciones().add(op);
        }

        preguntaRepository.save(p);
    }

    private record OpcionData(String texto, AreaInteres area) {}
}