package brian.duran.demo_shark_ruta_incidentes.controller;

import brian.duran.demo_shark_ruta_incidentes.dto.IncidenteDto;
import brian.duran.demo_shark_ruta_incidentes.dto.ReporteTramoDto;
import brian.duran.demo_shark_ruta_incidentes.service.IIncidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidentes")
public class IncidenteController {
    @Autowired
    IIncidenteService incidenteService;

    @GetMapping
    public ResponseEntity<List<IncidenteDto>> findAll() {
        return new ResponseEntity<>(incidenteService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenteDto> findById(@PathVariable String id) {
        return new ResponseEntity<>(incidenteService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/tipoIncidente")
    public ResponseEntity<List<IncidenteDto>> findByTipoIncidente(@RequestParam String tipoIncidente) {
        return new ResponseEntity<>(incidenteService.findByTipoIncidente(tipoIncidente) ,HttpStatus.OK);
    }

    @GetMapping("/tipoPrioridad")
    public ResponseEntity<List<IncidenteDto>> findByTipoPrioridad(@RequestParam int tipoPrioridad) {
        return new ResponseEntity<>(incidenteService.findByTipoPrioridad(tipoPrioridad) ,HttpStatus.OK);
    }

    @GetMapping("/reporte")
    public ResponseEntity<List<ReporteTramoDto>> getReporte(@RequestParam String nombreRuta) {
        return new ResponseEntity<>(incidenteService.obtenerReportePromedioGravedadPorRuta(nombreRuta),HttpStatus.OK);
    }

    @GetMapping("/rutas")
    public ResponseEntity<List<IncidenteDto>> findByNombreRuta(@RequestParam String nombreRuta) {
        return new ResponseEntity<>(incidenteService.findByNombreRuta(nombreRuta),HttpStatus.OK);
    }

    @GetMapping("/rutas/kilometro")
    public ResponseEntity<List<IncidenteDto>> findByNombreRutaAndKilometro(@RequestParam String nombreRuta, @RequestParam double kilometro) {
        return new ResponseEntity<>(incidenteService.findByNombreRutaAndKilometro(nombreRuta, kilometro),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<IncidenteDto> save(@RequestBody IncidenteDto incidenteDto){
        return new ResponseEntity<>(incidenteService.save(incidenteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        incidenteService.deleteById(id);
        return new ResponseEntity<>("Incidente eliminado correctamente." ,HttpStatus.OK);
    }
}
