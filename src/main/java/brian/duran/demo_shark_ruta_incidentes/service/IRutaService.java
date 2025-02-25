package brian.duran.demo_shark_ruta_incidentes.service;

import brian.duran.demo_shark_ruta_incidentes.dto.RutaDto;

import java.util.List;

public interface IRutaService {
    List<RutaDto> findAll();
    RutaDto findById(String id);
    RutaDto findByName(String nombre);

    RutaDto save(RutaDto ruta);
    RutaDto update(RutaDto ruta);

    void deleteById(String id);
    void deleteByName(String name);

}
