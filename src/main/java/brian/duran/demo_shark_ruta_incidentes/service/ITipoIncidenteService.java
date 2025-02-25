package brian.duran.demo_shark_ruta_incidentes.service;

import brian.duran.demo_shark_ruta_incidentes.dto.TipoIncidenteDto;

import java.util.List;

public interface ITipoIncidenteService {
    List<TipoIncidenteDto> findAll();
    TipoIncidenteDto findById(String id);
    List<TipoIncidenteDto>  findByTipoIncidente(String tipoIncidente);
    List<TipoIncidenteDto>  findByTipoPrioridad(int tipoPrioridad);

    TipoIncidenteDto save(TipoIncidenteDto tipoIncidenteDto);
    TipoIncidenteDto update(TipoIncidenteDto tipoIncidenteDto);

    void deleteById(String id);
}
