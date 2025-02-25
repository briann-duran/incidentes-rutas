package brian.duran.demo_shark_ruta_incidentes.init;

import brian.duran.demo_shark_ruta_incidentes.entity.Incidente;
import brian.duran.demo_shark_ruta_incidentes.entity.Ruta;
import brian.duran.demo_shark_ruta_incidentes.entity.TipoIncidente;
import brian.duran.demo_shark_ruta_incidentes.repository.IIncidenteRepository;
import brian.duran.demo_shark_ruta_incidentes.repository.IRutaRepository;
import brian.duran.demo_shark_ruta_incidentes.repository.ITipoIncidenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private final IRutaRepository rutaRepository;
    @Autowired
    private final IIncidenteRepository incidenteRepository;
    @Autowired
    private final ITipoIncidenteRepository tipoIncidenteRepository;

    @Value("${app.data.reset}")
    private boolean resetData;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Iniciando carga de datos...");

        if (resetData) {
            clearDatabase();
        }

        loadData();
    }

    private void clearDatabase() {
        log.info("🧹 Restableciendo la base de datos...");
        rutaRepository.deleteAll();
        tipoIncidenteRepository.deleteAll();
        incidenteRepository.deleteAll();
        log.info("✅ Base de datos restablecida.");
    }

    private void loadData() {
        if (rutaRepository.count() == 0) {
            // Rutas ...
            Ruta ruta1 = new Ruta("Ruta del Atlántico", "Retiro", "Mar del Plata", 400);
            Ruta ruta2 = new Ruta("Camino del Norte", "Rosario", "Córdoba", 380);
            Ruta ruta3 = new Ruta("Ruta Patagónica", "Bahía Blanca", "Puerto Madryn", 650);
            Ruta ruta4 = new Ruta("Ruta de los Lagos", "Bariloche", "San Martín de los Andes", 190);
            Ruta ruta5 = new Ruta("Ruta del Fin del Mundo", "Río Gallegos", "Ushuaia", 600);

            rutaRepository.saveAll(Arrays.asList(ruta1, ruta2, ruta3, ruta4, ruta5));

            // Establecer intersecciones entre las rutas
            ruta1.setIntersecciones(List.of(ruta3));
            ruta2.setIntersecciones(Arrays.asList(ruta4, ruta5));
            ruta3.setIntersecciones(List.of(ruta4));
            ruta4.setIntersecciones(null);
            ruta5.setIntersecciones(List.of(ruta1));

            // Log para verificar las intersecciones
            log.info("Intersecciones asignadas: Ruta1 -> {}", ruta1.getIntersecciones());

            // Volver a guardar las rutas con sus intersecciones
            rutaRepository.saveAll(Arrays.asList(ruta1, ruta2, ruta3, ruta4, ruta5));

            log.info("🎉 Datos de rutas y sus intersecciones cargados con éxito.");

            // Tipo de Incidentes ...
            TipoIncidente tipo1 = new TipoIncidente("Accidente", 1); // Usar cadena de texto para tipoIncidente y entero para tipoPrioridad
            TipoIncidente tipo2 = new TipoIncidente("Neblina", 2);
            TipoIncidente tipo3 = new TipoIncidente("Animales", 3);
            TipoIncidente tipo4 = new TipoIncidente("Bache", 4);
            TipoIncidente tipo5 = new TipoIncidente("Ruta Cortada", 5);

            tipoIncidenteRepository.saveAll(Arrays.asList(tipo1, tipo2, tipo3, tipo4, tipo5));

            // Incidentes ...
            Incidente incidente1 = new Incidente(ruta1, tipo1, 115.7, LocalDateTime.now(), "Accidente múltiple en la autopista.");
            Incidente incidente2 = new Incidente(ruta2, tipo2, 40.3, LocalDateTime.now().minusHours(3), "Densa neblina reduce la visibilidad.");
            Incidente incidente3 = new Incidente(ruta3, tipo3, 290.5, LocalDateTime.now().minusDays(1), "Grupo de ciervos cruzando la carretera.");
            Incidente incidente4 = new Incidente(ruta4, tipo4, 198.2, LocalDateTime.now().minusHours(6), "Bache peligroso en la curva.");
            Incidente incidente5 = new Incidente(ruta5, tipo5, 500.1, LocalDateTime.now().minusDays(2), "Corte total de la ruta debido a derrumbe.");
            Incidente incidente6 = new Incidente(ruta1, tipo5, 210.23, LocalDateTime.now().minusDays(2), "Corte total de la ruta debido a derrumbe.");
            Incidente incidente7 = new Incidente(ruta1, tipo2, 178.5, LocalDateTime.now().minusDays(2), "Densa neblina reduce la visibilidad.");
            Incidente incidente8 = new Incidente(ruta1, tipo3, 190.5, LocalDateTime.now().minusDays(2), "Grupo de ciervos cruzando la carretera.");

            List<Incidente> incidentes = Arrays.asList(incidente1, incidente2, incidente3, incidente4, incidente5, incidente6, incidente7, incidente8);

            incidenteRepository.saveAll(incidentes);
        } else {
            log.info("ℹ️ Las rutas y sus intersecciones ya están cargadas en la base de datos.");
        }
    }
}
