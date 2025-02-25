package brian.duran.demo_shark_ruta_incidentes.controller;

import brian.duran.demo_shark_ruta_incidentes.dto.RutaDto;
import brian.duran.demo_shark_ruta_incidentes.service.IRutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rutas")
public class RutaController {
    @Autowired
    private IRutaService rutaService;

    @GetMapping
    public ResponseEntity<List<RutaDto>> findAll(){
        return new ResponseEntity<>(rutaService.findAll(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/id")
    public ResponseEntity<RutaDto> findById(@RequestParam String id){
        return new ResponseEntity<>(rutaService.findById(id), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<RutaDto> findByName(@PathVariable String name){
        return new ResponseEntity<>(rutaService.findByName(name), HttpStatus.ACCEPTED);
    }

    @PostMapping
    public ResponseEntity<RutaDto> save(@RequestBody RutaDto rutaDto){
        return new ResponseEntity<>(rutaService.save(rutaDto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<RutaDto> update(@RequestBody RutaDto rutaDto){
        return new ResponseEntity<>(rutaService.update(rutaDto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteById(@RequestParam String id){
        rutaService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<Void> deleteByName(@PathVariable String nombre){
        rutaService.deleteByName(nombre);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
