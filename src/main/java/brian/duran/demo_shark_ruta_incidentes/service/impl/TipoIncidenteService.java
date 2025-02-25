package brian.duran.demo_shark_ruta_incidentes.service.impl;

import brian.duran.demo_shark_ruta_incidentes.dto.TipoIncidenteDto;
import brian.duran.demo_shark_ruta_incidentes.entity.TipoIncidente;
import brian.duran.demo_shark_ruta_incidentes.repository.ITipoIncidenteRepository;
import brian.duran.demo_shark_ruta_incidentes.service.ITipoIncidenteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TipoIncidenteService implements ITipoIncidenteService {
    @Autowired
    ITipoIncidenteRepository tipoIncidenteRepository;
    @Autowired
    ObjectMapper mapper;

    @Override
    public List<TipoIncidenteDto> findAll() {
        List<TipoIncidente> tipoIncidenteList = tipoIncidenteRepository.findAll();
        List<TipoIncidenteDto> tipoIncidenteDtoList = new ArrayList<>();

        tipoIncidenteList.forEach( x -> {
            tipoIncidenteDtoList.add(mapper.convertValue(x, TipoIncidenteDto.class));
        });

        return tipoIncidenteDtoList;
    }

    @Override
    public TipoIncidenteDto findById(String id) {
        TipoIncidente tipoIncidente = tipoIncidenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "Tipo de Incidente con ID '" + id + "' no se encuentra registrada."
        ));


        return mapper.convertValue(tipoIncidente, TipoIncidenteDto.class);
    }

    @Override
    public List<TipoIncidenteDto>  findByTipoIncidente(String tipoIncidente) {
        List<TipoIncidente> entities = tipoIncidenteRepository.findByTipoIncidente(tipoIncidente);

        if (entities.isEmpty()) {
            throw new RuntimeException("No se encontró el TipoIncidente con tipo: " + tipoIncidente);
        }

        return entities.stream()
                .map(entity -> mapper.convertValue(entity, TipoIncidenteDto.class))
                .collect(Collectors.toList());  }

    @Override
    public List<TipoIncidenteDto>  findByTipoPrioridad(int tipoPrioridad) {
        List<TipoIncidente> entities = tipoIncidenteRepository.findByTipoPrioridad(tipoPrioridad);

        if (entities.isEmpty()) {
            throw new RuntimeException("No se encontró el Tipo de Prioridad con un nivel de: " + tipoPrioridad);
        }

        return entities.stream()
                .map(entity -> mapper.convertValue(entity, TipoIncidenteDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TipoIncidenteDto save(TipoIncidenteDto tipoIncidenteDto) {
        if (tipoIncidenteDto.getId() != null) {
            Optional<TipoIncidente> existTipoIncidente = tipoIncidenteRepository.findById(tipoIncidenteDto.getId());
            if (existTipoIncidente.isPresent()) {
                throw new IllegalArgumentException("Tipo de Incidente con ID '" + tipoIncidenteDto.getId() + "' ya se encuentra registrada.");
            }
        }

        TipoIncidente tipoIncidente = mapper.convertValue(tipoIncidenteDto, TipoIncidente.class);
        tipoIncidenteRepository.save(tipoIncidente);

        return mapper.convertValue(tipoIncidente, TipoIncidenteDto.class);
    }

    @Override
    public TipoIncidenteDto update(TipoIncidenteDto tipoIncidenteDto) {
        if (tipoIncidenteDto.getId() != null) {
            Optional<TipoIncidente> existTipoIncidente = tipoIncidenteRepository.findById(tipoIncidenteDto.getId());
            if (existTipoIncidente.isEmpty()) {
                throw new IllegalArgumentException("Tipo de Incidente con ID '" + tipoIncidenteDto.getId() + "' ya se encuentra registrada.");
            }
        }

        TipoIncidente tipoIncidente = mapper.convertValue(tipoIncidenteDto, TipoIncidente.class);
        tipoIncidenteRepository.save(tipoIncidente);
        return mapper.convertValue(tipoIncidente, TipoIncidenteDto.class);
    }

    @Override
    public void deleteById(String id) {
        TipoIncidente tipoIncidente = tipoIncidenteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                "Tipo de Incidente con ID '" + id + "' no se encuentra registrada."
        ));

        tipoIncidenteRepository.deleteById(id);
    }
}
