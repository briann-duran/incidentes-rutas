package brian.duran.demo_shark_ruta_incidentes.service;

import brian.duran.demo_shark_ruta_incidentes.dto.IncidenteDto;
import brian.duran.demo_shark_ruta_incidentes.dto.ReporteTramoDto;


import java.util.List;

public interface IIncidenteService {
    List<IncidenteDto> findAll();
    IncidenteDto findById(String id);
    List<IncidenteDto> findByNombreRuta(String nombreRuta);
    List<IncidenteDto> findByNombreRutaAndKilometro(String nombreRuta, double kilometro);
    List<ReporteTramoDto> obtenerReportePromedioGravedadPorRuta(String nombreRuta);
    List<IncidenteDto> findByTipoIncidente(String tipoIncidente);
    List<IncidenteDto>  findByTipoPrioridad(int tipoPrioridad);

    IncidenteDto save(IncidenteDto incidenteDto);
    IncidenteDto update(IncidenteDto incidenteDto);

    void deleteById(String id);
}
