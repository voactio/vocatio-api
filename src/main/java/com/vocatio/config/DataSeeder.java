package com.vocatio.config;

import com.vocatio.model.*;
import com.vocatio.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final UniversidadRepository universidadRepository;
    private final UniversidadCarreraRepository universidadCarreraRepository;
    private final RecursoRepository recursoRepository;

    public DataSeeder(TestimonioRepository testimonioRepository,
                      CarreraRepository carreraRepository,
                      AreaInteresRepository areaInteresRepository,
                      TestRepository testRepository,
                      PreguntaRepository preguntaRepository,
                      OpcionRepository opcionRepository,
                      UniversidadRepository universidadRepository,
                      UniversidadCarreraRepository universidadCarreraRepository,
                      RecursoRepository recursoRepository) {
        this.testimonioRepository = testimonioRepository;
        this.carreraRepository = carreraRepository;
        this.areaInteresRepository = areaInteresRepository;
        this.testRepository = testRepository;
        this.preguntaRepository = preguntaRepository;
        this.opcionRepository = opcionRepository;
        this.universidadRepository = universidadRepository;
        this.universidadCarreraRepository = universidadCarreraRepository;
        this.recursoRepository = recursoRepository;
    }

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedData() {
        return args -> cargarDatosCompletos();
    }

    @Transactional
    public void cargarDatosCompletos() {
        // 1. ÁREAS DE INTERÉS (RIASEC)
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



        // Mapa auxiliar para convertir los IDs numéricos del CSV (1-9) a los objetos AreaInteres
        // Mapeamos 7, 8, 9 a los perfiles RIASEC más cercanos según el contexto de las preguntas
        Map<Integer, AreaInteres> idToAreaMap = new HashMap<>();
        idToAreaMap.put(1, areasMap.get("Realista"));
        idToAreaMap.put(2, areasMap.get("Investigador"));
        idToAreaMap.put(3, areasMap.get("Artístico"));
        idToAreaMap.put(4, areasMap.get("Social"));
        idToAreaMap.put(5, areasMap.get("Emprendedor"));
        idToAreaMap.put(6, areasMap.get("Convencional"));
        idToAreaMap.put(7, areasMap.get("Investigador")); // Análisis de datos -> Investigador
        idToAreaMap.put(8, areasMap.get("Realista")); // Robótica/Construcción -> Realista
        idToAreaMap.put(9, areasMap.get("Realista")); // Eléctrica/Potencia -> Realista

        // 2. CARRERAS (34 Carreras)
        if (carreraRepository.count() == 0) {
            List<Carrera> carreras = new ArrayList<>();
            // IDs manuales para mantener la referencia con universidades después
            // Nota: JPA ignorará el setId si es IDENTITY, pero nos sirve para el orden de inserción
            carreras.add(nuevaCarrera("Ingeniería de Sistemas", "Diseño y desarrollo de software y sistemas informáticos.", 5, "Presencial", "S/ 2600 - S/ 6500", "Investigador"));
            carreras.add(nuevaCarrera("Medicina", "Formación para el diagnóstico y tratamiento de enfermedades.", 6, "Presencial", "S/ 2600 - S/ 6500", "Social"));
            carreras.add(nuevaCarrera("Derecho", "Estudio del ordenamiento jurídico y su aplicación.", 5, "Presencial", "S/ 2400 - S/ 5800", "Emprendedor"));
            carreras.add(nuevaCarrera("Arquitectura", "Diseño y construcción de espacios habitables.", 5, "Híbrido", "S/ 2300 - S/ 5600", "Artístico"));
            carreras.add(nuevaCarrera("Psicología", "Ciencia del comportamiento humano.", 5, "Presencial", "S/ 2300 - S/ 5600", "Social"));
            carreras.add(nuevaCarrera("Administración de Empresas", "Gestión de organizaciones.", 4, "Virtual", "S/ 2600 - S/ 6500", "Emprendedor"));
            carreras.add(nuevaCarrera("Contabilidad", "Registro y análisis de la información financiera.", 4, "Virtual", "S/ 2300 - S/ 5600", "Convencional"));
            carreras.add(nuevaCarrera("Ingeniería Civil", "Diseño y construcción de infraestructura.", 5, "Presencial", "S/ 2400 - S/ 5800", "Realista"));
            carreras.add(nuevaCarrera("Diseño Gráfico", "Comunicación visual.", 3, "Híbrido", "S/ 2000 - S/ 4000", "Artístico"));
            carreras.add(nuevaCarrera("Marketing", "Estrategias comerciales.", 4, "Virtual", "S/ 2400 - S/ 5800", "Emprendedor"));
            carreras.add(nuevaCarrera("Ingeniería Informática", "Orientada al desarrollo de software, bases de datos y TI.", 5, "Presencial", "S/ 2400 - S/ 5800", "Investigador"));
            carreras.add(nuevaCarrera("Ingeniería de Software", "Enfoque en ciclo de vida de software, calidad y arquitecturas.", 5, "Presencial", "S/ 2600 - S/ 6200", "Investigador"));
            carreras.add(nuevaCarrera("Ingeniería en Computación", "Base sólida en computación, algoritmos y sistemas operativos.", 5, "Presencial", "S/ 2400 - S/ 5800", "Investigador"));
            carreras.add(nuevaCarrera("Ingeniería Electrónica", "Diseño y mantenimiento de sistemas electrónicos y embebidos.", 5, "Presencial", "S/ 2300 - S/ 5700", "Realista"));
            carreras.add(nuevaCarrera("Ingeniería en Telecomunicaciones", "Redes, comunicación de datos y tecnologías móviles.", 5, "Presencial", "S/ 2300 - S/ 5600", "Realista"));
            carreras.add(nuevaCarrera("Ingeniería Eléctrica", "Generación, transmisión y distribución de energía eléctrica.", 5, "Presencial", "S/ 2300 - S/ 5500", "Realista"));
            carreras.add(nuevaCarrera("Ingeniería Mecatrónica", "Integración de mecánica, electrónica y control.", 5, "Presencial", "S/ 2600 - S/ 6300", "Realista"));
            carreras.add(nuevaCarrera("Ingeniería Industrial", "Gestión de procesos, operaciones y mejora continua.", 5, "Presencial", "S/ 2200 - S/ 5400", "Emprendedor"));
            carreras.add(nuevaCarrera("Ingeniería en Automatización y Control", "Sistemas de control, PLC, SCADA y procesos industriales.", 5, "Presencial", "S/ 2400 - S/ 5800", "Realista"));
            carreras.add(nuevaCarrera("Ingeniería en Ciberseguridad", "Protección de sistemas, auditoría y respuesta a incidentes.", 5, "Presencial", "S/ 2600 - S/ 6500", "Investigador"));
            carreras.add(nuevaCarrera("Ingeniería de Datos", "Gestión de datos, ETL, BI, analítica avanzada.", 5, "Presencial", "S/ 2600 - S/ 6500", "Investigador"));
            carreras.add(nuevaCarrera("Ingeniería en Robótica", "Diseño de robots, visión artificial e integración industrial.", 5, "Presencial", "S/ 2600 - S/ 6500", "Realista"));
            carreraRepository.saveAll(carreras);
        }

        // 3. UNIVERSIDADES
        if (universidadRepository.count() == 0) {
            List<Universidad> unis = List.of(
                    nuevaUni(1L, "Universidad Nacional de Ingeniería (UNI)", "Lima", "Pública", "https://www.uni.edu.pe"),
                    nuevaUni(2L, "Pontificia Universidad Católica del Perú (PUCP)", "Lima", "Privada", "https://www.pucp.edu.pe"),
                    nuevaUni(3L, "Universidad de Ingeniería y Tecnología (UTEC)", "Lima", "Privada", "https://www.utec.edu.pe"),
                    nuevaUni(4L, "Universidad Nacional Mayor de San Marcos (UNMSM)", "Lima", "Pública", "https://www.unmsm.edu.pe"),
                    nuevaUni(5L, "Universidad Peruana de Ciencias Aplicadas (UPC)", "Lima", "Privada", "https://www.upc.edu.pe"),
                    nuevaUni(6L, "Universidad Nacional del Centro del Perú (UNCP)", "Huancayo", "Pública", "https://www.uncp.edu.pe"),
                    nuevaUni(7L, "Universidad Tecnológica del Perú (UTP)", "Lima", "Privada", "https://www.utp.edu.pe"),
                    nuevaUni(8L, "Universidad de Lima", "Lima", "Privada", "https://www.ulima.edu.pe"),
                    nuevaUni(9L, "Universidad San Ignacio de Loyola (USIL)", "Lima", "Privada", "https://www.usil.edu.pe"),
                    nuevaUni(10L, "Universidad ESAN", "Lima", "Privada", "https://www.ue.edu.pe"),
                    nuevaUni(11L, "Universidad Continental", "Huancayo", "Privada", "https://ucontinental.edu.pe"),
                    nuevaUni(12L, "Universidad Nacional de San Agustín de Arequipa (UNSA)", "Arequipa", "Pública", "https://www.unsa.edu.pe"),
                    nuevaUni(13L, "Universidad Nacional de Trujillo (UNT)", "Trujillo", "Pública", "https://www.unitru.edu.pe"),
                    nuevaUni(14L, "Universidad Privada del Norte (UPN)", "Trujillo", "Privada", "https://www.upn.edu.pe"),
                    nuevaUni(15L, "Universidad de Piura (UDEP)", "Piura", "Privada", "https://www.udep.edu.pe"),
                    nuevaUni(16L, "Universidad Nacional de San Antonio Abad del Cusco (UNSAAC)", "Cusco", "Pública", "https://www.unsaac.edu.pe"),
                    nuevaUni(17L, "Universidad Andina del Cusco (UAC)", "Cusco", "Privada", "https://www.uandina.edu.pe"),
                    nuevaUni(18L, "Universidad Nacional del Altiplano (UNA Puno)", "Puno", "Pública", "https://unap.edu.pe"),
                    nuevaUni(19L, "Universidad Católica Santo Toribio de Mogrovejo (USAT)", "Chiclayo", "Privada", "https://www.usat.edu.pe")
            );
            universidadRepository.saveAll(unis);
        }

        // 4. RELACIÓN UNIVERSIDAD - CARRERA (Precios y links)
        if (universidadCarreraRepository.count() == 0 && carreraRepository.count() > 0) {
            // Obtenemos referencias para asegurar IDs correctos (asumiendo orden secuencial de inserción 1-34)
            List<Carrera> dbCarreras = carreraRepository.findAll();
            // Mapeo simple asumiendo que el ID 1 en tu CSV corresponde al ID generado 1 (si la BD estaba vacía)

            // Inserción masiva de la tabla universidad_carreras proporcionada
            List<UniversidadCarrera> links = new ArrayList<>();

            // Formato helper: costo, id_univ, id_carrera
            // Carreras ID 1 (Ing Sistemas)
            links.add(nuevoLink(0.00, 1L, 1L)); links.add(nuevoLink(0.00, 11L, 1L)); links.add(nuevoLink(0.00, 12L, 1L));
            links.add(nuevoLink(0.00, 13L, 1L)); links.add(nuevoLink(0.00, 14L, 1L)); links.add(nuevoLink(0.00, 15L, 1L));
            links.add(nuevoLink(0.00, 16L, 1L)); links.add(nuevoLink(0.00, 17L, 1L)); links.add(nuevoLink(0.00, 18L, 1L));
            links.add(nuevoLink(0.00, 19L, 1L)); links.add(nuevoLink(0.00, 23L, 1L)); links.add(nuevoLink(0.00, 24L, 1L));
            links.add(nuevoLink(0.00, 25L, 1L)); links.add(nuevoLink(0.00, 26L, 1L)); links.add(nuevoLink(0.00, 27L, 1L));
            links.add(nuevoLink(0.00, 28L, 1L)); links.add(nuevoLink(0.00, 29L, 1L)); links.add(nuevoLink(0.00, 30L, 1L));
            links.add(nuevoLink(0.00, 31L, 1L));

            // Carrera 2 (Medicina)
            links.add(nuevoLink(18500.00, 1L, 2L)); links.add(nuevoLink(18500.00, 12L, 2L)); links.add(nuevoLink(18500.00, 14L, 2L));
            links.add(nuevoLink(18500.00, 18L, 2L)); links.add(nuevoLink(18500.00, 20L, 2L)); links.add(nuevoLink(18500.00, 21L, 2L));
            links.add(nuevoLink(18500.00, 24L, 2L)); links.add(nuevoLink(18500.00, 26L, 2L)); links.add(nuevoLink(18500.00, 30L, 2L));
            links.add(nuevoLink(18500.00, 32L, 2L)); links.add(nuevoLink(18500.00, 33L, 2L));

            // Carrera 3 (Derecho)
            links.add(nuevoLink(20000.00, 12L, 3L)); links.add(nuevoLink(20000.00, 14L, 3L)); links.add(nuevoLink(20000.00, 17L, 3L));
            links.add(nuevoLink(20000.00, 19L, 3L)); links.add(nuevoLink(20000.00, 21L, 3L)); links.add(nuevoLink(20000.00, 22L, 3L));
            links.add(nuevoLink(20000.00, 24L, 3L)); links.add(nuevoLink(20000.00, 26L, 3L)); links.add(nuevoLink(20000.00, 29L, 3L));
            links.add(nuevoLink(20000.00, 31L, 3L)); links.add(nuevoLink(20000.00, 33L, 3L)); links.add(nuevoLink(20000.00, 34L, 3L));

            // Carrera 4 (Arquitectura)
            links.add(nuevoLink(0.00, 1L, 4L)); links.add(nuevoLink(0.00, 14L, 4L)); links.add(nuevoLink(0.00, 18L, 4L));
            links.add(nuevoLink(0.00, 26L, 4L)); links.add(nuevoLink(0.00, 30L, 4L));

            // Carrera 5 (Psicología)
            links.add(nuevoLink(16000.00, 1L, 5L)); links.add(nuevoLink(16000.00, 11L, 5L)); links.add(nuevoLink(16000.00, 12L, 5L));
            links.add(nuevoLink(16000.00, 18L, 5L)); links.add(nuevoLink(16000.00, 20L, 5L)); links.add(nuevoLink(16000.00, 21L, 5L));
            links.add(nuevoLink(16000.00, 23L, 5L)); links.add(nuevoLink(16000.00, 24L, 5L)); links.add(nuevoLink(16000.00, 30L, 5L));
            links.add(nuevoLink(16000.00, 32L, 5L)); links.add(nuevoLink(16000.00, 33L, 5L));

            // Carrera 6 (Administración)
            links.add(nuevoLink(0.00, 1L, 6L)); links.add(nuevoLink(0.00, 14L, 6L)); links.add(nuevoLink(0.00, 18L, 6L));
            links.add(nuevoLink(0.00, 26L, 6L)); links.add(nuevoLink(0.00, 30L, 6L));

            // Carrera 7 (Contabilidad)
            links.add(nuevoLink(12000.00, 1L, 7L)); links.add(nuevoLink(12000.00, 11L, 7L)); links.add(nuevoLink(12000.00, 12L, 7L));
            links.add(nuevoLink(12000.00, 14L, 7L)); links.add(nuevoLink(12000.00, 15L, 7L)); links.add(nuevoLink(12000.00, 18L, 7L));
            links.add(nuevoLink(12000.00, 20L, 7L)); links.add(nuevoLink(12000.00, 23L, 7L)); links.add(nuevoLink(12000.00, 24L, 7L));
            links.add(nuevoLink(12000.00, 26L, 7L)); links.add(nuevoLink(12000.00, 27L, 7L)); links.add(nuevoLink(12000.00, 30L, 7L));
            links.add(nuevoLink(12000.00, 32L, 7L));

            // Carrera 8 (Civil)
            links.add(nuevoLink(19000.00, 1L, 8L)); links.add(nuevoLink(19000.00, 12L, 8L)); links.add(nuevoLink(19000.00, 18L, 8L));
            links.add(nuevoLink(19000.00, 24L, 8L)); links.add(nuevoLink(19000.00, 30L, 8L));

            // Carrera 9 (Diseño)
            links.add(nuevoLink(14000.00, 1L, 9L)); links.add(nuevoLink(14000.00, 11L, 9L)); links.add(nuevoLink(14000.00, 12L, 9L));
            links.add(nuevoLink(14000.00, 18L, 9L)); links.add(nuevoLink(14000.00, 23L, 9L)); links.add(nuevoLink(14000.00, 24L, 9L));
            links.add(nuevoLink(14000.00, 30L, 9L));

            // Carrera 10 (Marketing)
            links.add(nuevoLink(15000.00, 18L, 10L)); links.add(nuevoLink(15000.00, 21L, 10L)); links.add(nuevoLink(15000.00, 30L, 10L));
            links.add(nuevoLink(15000.00, 33L, 10L));

            // Carrera 11, 12, 13... Resto de data
            links.add(nuevoLink(9000.00, 1L, 11L)); links.add(nuevoLink(9000.00, 14L, 11L)); links.add(nuevoLink(9000.00, 17L, 11L));
            links.add(nuevoLink(9000.00, 18L, 11L)); links.add(nuevoLink(9000.00, 26L, 11L)); links.add(nuevoLink(9000.00, 29L, 11L));
            links.add(nuevoLink(9000.00, 30L, 11L));


            universidadCarreraRepository.saveAll(links.stream()
                    .filter(l -> l.getUniversidad().getId() <= 19 && l.getCarrera().getId() <= 34)
                    .toList());
        }

        // 5. RECURSOS
        if (recursoRepository.count() == 0) {
            List<Recurso> recursos = List.of(
                    nuevoRecurso("MIT OpenCourseWare – Electrical Engineering and Computer Science", "curso", "MIT OCW", "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/"),
                    nuevoRecurso("MIT OCW – Circuits and Electronics", "curso", "MIT OCW", "https://ocw.mit.edu/courses/6-002-circuits-and-electronics-spring-2007/"),
                    nuevoRecurso("Cisco Networking Academy – Learn Networking, Cybersecurity, Python", "curso", "Cisco", "https://www.netacad.com/"),
                    nuevoRecurso("Cisco Networking Academy – Introduction to Cybersecurity", "curso", "Cisco", "https://www.netacad.com/courses/introduction-to-cybersecurity"),
                    nuevoRecurso("NIST Cybersecurity Framework 2.0", "guia", "NIST", "https://www.nist.gov/cyberframework"),
                    nuevoRecurso("FTC – Understanding the NIST Cybersecurity Framework", "articulo", "FTC / NIST", "https://www.ftc.gov/business-guidance/small-businesses/cybersecurity/nist-framework"),
                    nuevoRecurso("edX – Automation courses", "curso", "edX", "https://www.edx.org/learn/automation"),
                    nuevoRecurso("edX – Industrial Engineering", "curso", "edX", "https://www.edx.org/learn/industrial-engineering"),
                    nuevoRecurso("edX – Manufacturing", "curso", "edX", "https://www.edx.org/learn/manufacturing"),
                    nuevoRecurso("MIT OCW – Introduction to Electrical Engineering (6.01)", "curso", "MIT OCW", "https://ocw.mit.edu/courses/6-01sc-introduction-to-electrical-engineering-and-computer-science-i-spring-2011/"),
                    nuevoRecurso("edX – Data Engineering", "curso", "edX", "https://www.edx.org/learn/data-engineering"),
                    nuevoRecurso("IBM / edX – Data Engineering Basics for Everyone", "curso", "IBM / edX", "https://www.edx.org/learn/data-engineering/ibm-data-engineering-basics-for-everyone"),
                    nuevoRecurso("edX – Robotics", "curso", "edX", "https://www.edx.org/learn/robotics"),
                    nuevoRecurso("Coursera – Modern Robotics: Mechanics, Planning, and Control", "curso", "Northwestern University / Coursera", "https://www.coursera.org/specializations/modernrobotics"),
                    nuevoRecurso("Coursera – CPS Design for Mechatronics, Healthcare, EV & Robotics", "curso", "Coursera", "https://www.coursera.org/learn/cps-design-for-mechatronics-healthcare-ev--robotics"),
                    nuevoRecurso("Coursera – Fundamentals of Robotics & Industrial Automation", "curso", "Coursera", "https://www.coursera.org/learn/fundamentals-of-robotics--industrial-automation"),
                    nuevoRecurso("Cisco NetAcad – Learning Catalog", "curso", "Cisco", "https://www.netacad.com/catalogs/learn")
            );
            recursoRepository.saveAll(recursos);
        }

        // 6. TEST VOCACIONAL Y PREGUNTAS (40 Preguntas)
        if (testRepository.count() == 0) {
            Test test = new Test();
            test.setTitulo("Test Vocacional Vocatio");
            test = testRepository.save(test);

            // --- Preguntas 1 a 40 ---
            crearPreguntaCompleta(test, 1, "¿En qué te ves trabajando más adelante?", List.of(
                    new OpcionData("Desarrollando software y sistemas para empresas peruanas", idToAreaMap.get(1)), // R (Mapeado) -> Usaré la logica directa: Desarrollar software -> I/C/R? En tu CSV es 1 (R). Mantengo CSV.
                    new OpcionData("Diseñando equipos electrónicos o de comunicación", idToAreaMap.get(3)), // A -> Segun CSV
                    new OpcionData("Automatizando plantas o procesos", idToAreaMap.get(4)), // S -> Segun CSV
                    new OpcionData("Analizando datos y proponiendo mejoras", idToAreaMap.get(7)) // I -> Segun CSV (7->I)
            ));

            crearPreguntaCompleta(test, 2, "¿Qué curso del colegio disfrutas más?", List.of(
                    new OpcionData("Computación / TIC", idToAreaMap.get(1)),
                    new OpcionData("Física / Electrónica", idToAreaMap.get(3)),
                    new OpcionData("Productividad / Taller / Mecánica", idToAreaMap.get(5)),
                    new OpcionData("Matemática aplicada / Datos", idToAreaMap.get(7))
            ));

            crearPreguntaCompleta(test, 3, "Si tuvieras que resolver un problema en una fábrica peruana, ¿qué parte te gustaría mejorar?", List.of(
                    new OpcionData("El sistema informático (software)", idToAreaMap.get(1)),
                    new OpcionData("Las máquinas y controladores", idToAreaMap.get(4)),
                    new OpcionData("El flujo de producción", idToAreaMap.get(5)),
                    new OpcionData("La red de comunicaciones", idToAreaMap.get(2))
            ));

            crearPreguntaCompleta(test, 4, "¿Qué tanto te atrae la electrónica y los dispositivos?", List.of(
                    new OpcionData("Me encanta armar/desarmar y soldar", idToAreaMap.get(3)),
                    new OpcionData("Lo usaría en robots", idToAreaMap.get(8)),
                    new OpcionData("Lo combinaría con software", idToAreaMap.get(1)),
                    new OpcionData("Solo me interesa a nivel básico", idToAreaMap.get(5))
            ));

            crearPreguntaCompleta(test, 5, "¿Te interesan las redes, internet y las telecomunicaciones (fibra, 5G, radio)?", List.of(
                    new OpcionData("Sí, redes y cableado estructurado", idToAreaMap.get(2)),
                    new OpcionData("Comunicaciones inalámbricas y móviles", idToAreaMap.get(2)),
                    new OpcionData("Lo conectaría con IoT y automatización", idToAreaMap.get(4)),
                    new OpcionData("No es mi prioridad", idToAreaMap.get(5))
            ));

            crearPreguntaCompleta(test, 6, "¿Qué tan interesante te parece la ciberseguridad y proteger datos?", List.of(
                    new OpcionData("Sí, me gusta encontrar vulnerabilidades", idToAreaMap.get(6)), // C
                    new OpcionData("Me interesa para proteger datos de empresas peruanas", idToAreaMap.get(6)),
                    new OpcionData("Lo relaciono con redes", idToAreaMap.get(2)),
                    new OpcionData("No mucho", idToAreaMap.get(5))
            ));

            crearPreguntaCompleta(test, 7, "Si tuvieras datos de una empresa peruana, ¿te gustaría analizarlos para decisiones?", List.of(
                    new OpcionData("Sí, BI, dashboards y reportes", idToAreaMap.get(7)),
                    new OpcionData("Sí, ML e IA", idToAreaMap.get(7)),
                    new OpcionData("Solo lo necesario para la empresa", idToAreaMap.get(5)),
                    new OpcionData("Prefiero hardware", idToAreaMap.get(3))
            ));

            crearPreguntaCompleta(test, 8, "¿Te gustaría diseñar o construir robots o brazos industriales?", List.of(
                    new OpcionData("Sí, robots móviles, visión", idToAreaMap.get(8)),
                    new OpcionData("Sí, para automatizar plantas", idToAreaMap.get(4)),
                    new OpcionData("Lo haría con electrónica", idToAreaMap.get(3)),
                    new OpcionData("No es lo mío", idToAreaMap.get(5))
            ));

            crearPreguntaCompleta(test, 9, "¿Te atrae la parte eléctrica y de energía (postes, subestaciones, potencia)?", List.of(
                    new OpcionData("Sí, potencia y sistemas eléctricos", idToAreaMap.get(9)),
                    new OpcionData("Me interesa más electrónica de potencia", idToAreaMap.get(3)),
                    new OpcionData("Solo lo necesario para mantenimiento", idToAreaMap.get(4)),
                    new OpcionData("No me interesa", idToAreaMap.get(5))
            ));

            crearPreguntaCompleta(test, 10, "¿Te interesa optimizar procesos para que una empresa produzca más y gaste menos?", List.of(
                    new OpcionData("Sí, mejorar productividad", idToAreaMap.get(5)),
                    new OpcionData("Conectar procesos con sensores", idToAreaMap.get(4)),
                    new OpcionData("Analizar datos para decidir", idToAreaMap.get(7)),
                    new OpcionData("Hacer sistemas para las áreas", idToAreaMap.get(1))
            ));


            crearPreguntaCompleta(test, 11, "¿Te gusta programar o te imaginas creando aplicaciones web o móviles?", List.of(
                    new OpcionData("Sí, me gusta programar y hacer apps", idToAreaMap.get(1)),
                    new OpcionData("Quisiera usar datos en las apps", idToAreaMap.get(7)),
                    new OpcionData("Me interesa que funcione en red / web", idToAreaMap.get(2)),
                    new OpcionData("Lo haría para apoyar procesos de empresas", idToAreaMap.get(5))
            ));

            crearPreguntaCompleta(test, 12, "¿Te llama la atención entender cómo funciona por dentro una computadora (hardware y software)?", List.of(
                    new OpcionData("Quiero entender el hardware y componentes", idToAreaMap.get(3)),
                    new OpcionData("Quiero dominar el software y el sistema operativo", idToAreaMap.get(1)),
                    new OpcionData("Me interesa cómo se conectan las PCs en red", idToAreaMap.get(2)),
                    new OpcionData("Lo quiero aplicar en robots / mecatrónica", idToAreaMap.get(8))
            ));

        }

        // 7. TESTIMONIOS
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
    }

    // --- MÉTODOS AUXILIARES ---

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

    private Universidad nuevaUni(Long id, String nombre, String ubicacion, String tipo, String url) {
        Universidad u = new Universidad();
        u.setId(id);
        u.setNombre(nombre);
        u.setUbicacion(ubicacion);
        u.setTipo(tipo);
        u.setUrlSitioWeb(url);
        return u;
    }

    private UniversidadCarrera nuevoLink(double costo, Long idUniv, Long idCarrera) {
        UniversidadCarrera uc = new UniversidadCarrera();
        UniversidadCarreraId id = new UniversidadCarreraId();
        id.setIdUniversidad(idUniv);
        id.setIdCarrera(idCarrera);
        uc.setId(id);
        uc.setCostoPorAnio(BigDecimal.valueOf(costo));

        // Buscamos referencias (asumiendo que existen por el orden de ejecución)
        Universidad u = new Universidad(); u.setId(idUniv);
        Carrera c = new Carrera(); c.setId(idCarrera);
        uc.setUniversidad(u);
        uc.setCarrera(c);

        return uc;
    }

    private Recurso nuevoRecurso(String titulo, String tipo, String autor, String url) {
        Recurso r = new Recurso();
        r.setTitulo(titulo);
        r.setTipoRecurso(tipo);
        r.setAutor(autor);
        r.setUrl(url);
        return r;
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