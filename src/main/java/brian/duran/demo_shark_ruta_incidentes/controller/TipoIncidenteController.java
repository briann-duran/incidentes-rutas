package brian.duran.demo_shark_ruta_incidentes.controller;

import brian.duran.demo_shark_ruta_incidentes.dto.TipoIncidenteDto;
import brian.duran.demo_shark_ruta_incidentes.service.ITipoIncidenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipo_incidentes")
public class TipoIncidenteController {
    @Autowired
    ITipoIncidenteService tipoIncidenteService;

    @GetMapping
    public ResponseEntity<List<TipoIncidenteDto>> findAll() {
        return new ResponseEntity<>(tipoIncidenteService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoIncidenteDto> findById(@PathVariable String id) {
        return new ResponseEntity<>(tipoIncidenteService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<TipoIncidenteDto>> findByTipoIncidente(@RequestParam String tipoIncidente) {
        return new ResponseEntity<>(tipoIncidenteService.findByTipoIncidente(tipoIncidente), HttpStatus.OK);
    }

    @GetMapping("/prioridad")
    public ResponseEntity<List<TipoIncidenteDto>> findByTipoPrioridad(@RequestParam int tipoPrioridad) {
        return new ResponseEntity<>(tipoIncidenteService.findByTipoPrioridad(tipoPrioridad), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TipoIncidenteDto> save(@RequestBody TipoIncidenteDto tipoIncidenteDto){
        return new ResponseEntity<>(tipoIncidenteService.save(tipoIncidenteDto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<TipoIncidenteDto> update(@RequestBody TipoIncidenteDto tipoIncidenteDto){
        return new ResponseEntity<>(tipoIncidenteService.update(tipoIncidenteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable String id) {
        tipoIncidenteService.deleteById(id);
        return new ResponseEntity<>("Tipo de incidente eliminado correctamente." ,HttpStatus.OK);
    }
}
