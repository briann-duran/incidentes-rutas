package brian.duran.demo_shark_ruta_incidentes.service.impl;

import brian.duran.demo_shark_ruta_incidentes.dto.IncidenteDto;
import brian.duran.demo_shark_ruta_incidentes.dto.ReporteTramoDto;
import brian.duran.demo_shark_ruta_incidentes.dto.RutaDto;
import brian.duran.demo_shark_ruta_incidentes.entity.Incidente;
import brian.duran.demo_shark_ruta_incidentes.entity.Ruta;
import brian.duran.demo_shark_ruta_incidentes.entity.TipoIncidente;
import brian.duran.demo_shark_ruta_incidentes.repository.IIncidenteRepository;
import brian.duran.demo_shark_ruta_incidentes.repository.IRutaRepository;
import brian.duran.demo_shark_ruta_incidentes.repository.ITipoIncidenteRepository;
import brian.duran.demo_shark_ruta_incidentes.service.IIncidenteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class IncidenteService implements IIncidenteService {
    @Autowired
    IIncidenteRepository incidenteRepository;
    @Autowired
    ITipoIncidenteRepository tipoIncidenteRepository;
    @Autowired
    IRutaRepository rutaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final Map<String, Integer> rutaConsultaContador = new ConcurrentHashMap<>();
    private static final int CONSULTA_MAX = 5;
    private static final String CACHE_KEY_PREFIX = "cache1::";


    @Override
    public List<IncidenteDto> findAll() {

        String cacheKey = "allIncidentes";

        List<IncidenteDto> incidenteDtoList = (List<IncidenteDto>) redisTemplate.opsForValue().get(cacheKey);

        // Si el cache no contiene los datos, los obtengo de la base de datos
        if (incidenteDtoList == null) {
            List<Incidente> incidenteList = incidenteRepository.findAll();
            incidenteDtoList = new ArrayList<>();

            for (Incidente incidente : incidenteList) {
                IncidenteDto incidenteDto = new IncidenteDto();
                incidenteDto.setId(incidente.getId());
                incidenteDto.setTipoIncidente(incidente.getTipoIncidente());
                incidenteDto.setKilometro(incidente.getKilometro());
                incidenteDto.setTimestamp(incidente.getTimestamp());
                incidenteDto.setComentarios(incidente.getComentarios());

                if (incidente.getRuta() != null) {
                    RutaDto rutaDto = new RutaDto();
                    rutaDto.setId(incidente.getRuta().getId());
                    rutaDto.setNombre(incidente.getRuta().getNombre());
                    rutaDto.setOrigen(incidente.getRuta().getOrigen());
                    rutaDto.setDestino(incidente.getRuta().getDestino());
                    rutaDto.setDistancia(incidente.getRuta().getDistancia());

                    if (incidente.getRuta().getIntersecciones() != null) {
                        List<RutaDto> interseccionesDto = new ArrayList<>();
                        for (Ruta interseccion : incidente.getRuta().getIntersecciones()) {
                            RutaDto interseccionDto = new RutaDto();
                            interseccionDto.setId(interseccion.getId());
                            interseccionDto.setNombre(interseccion.getNombre());
                            interseccionDto.setOrigen(interseccion.getOrigen());
                            interseccionDto.setDestino(interseccion.getDestino());
                            interseccionDto.setDistancia(interseccion.getDistancia());
                            interseccionesDto.add(interseccionDto);
                        }
                        rutaDto.setIntersecciones(interseccionesDto);
                    }

                    incidenteDto.setRuta(rutaDto);
                }

                incidenteDtoList.add(incidenteDto);
            }

            redisTemplate.delete(cacheKey);
            redisTemplate.opsForValue().set(cacheKey, incidenteDtoList, Duration.ofDays(1));
        }

        return incidenteDtoList;
    }


    @Override
    public IncidenteDto findById(String id) {
        String cacheKey = "incidente_" + id;

        IncidenteDto incidenteDto = (IncidenteDto) redisTemplate.opsForValue().get(cacheKey);

        // Si no está en cache, consulto en la base de datos
        if (incidenteDto == null) {
            Incidente incidente = incidenteRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Incidente con ID '" + id + "' no se encuentra registrado."
                    ));

            incidenteDto = new IncidenteDto();
            incidenteDto.setId(incidente.getId());
            incidenteDto.setTipoIncidente(incidente.getTipoIncidente());
            incidenteDto.setKilometro(incidente.getKilometro());
            incidenteDto.setTimestamp(incidente.getTimestamp());
            incidenteDto.setComentarios(incidente.getComentarios());

            if (incidente.getRuta() != null) {
                RutaDto rutaDto = new RutaDto();
                rutaDto.setId(incidente.getRuta().getId());
                rutaDto.setNombre(incidente.getRuta().getNombre());
                rutaDto.setOrigen(incidente.getRuta().getOrigen());
                rutaDto.setDestino(incidente.getRuta().getDestino());
                rutaDto.setDistancia(incidente.getRuta().getDistancia());

                if (incidente.getRuta().getIntersecciones() != null) {
                    List<RutaDto> interseccionesDto = new ArrayList<>();
                    for (Ruta interseccion : incidente.getRuta().getIntersecciones()) {
                        RutaDto interseccionDto = new RutaDto();
                        interseccionDto.setId(interseccion.getId());
                        interseccionDto.setNombre(interseccion.getNombre());
                        interseccionDto.setOrigen(interseccion.getOrigen());
                        interseccionDto.setDestino(interseccion.getDestino());
                        interseccionDto.setDistancia(interseccion.getDistancia());
                        interseccionesDto.add(interseccionDto);
                    }
                    rutaDto.setIntersecciones(interseccionesDto);
                }
                incidenteDto.setRuta(rutaDto);
            }

            // Guardamos en cache con una expiración de 1 día
            redisTemplate.opsForValue().set(cacheKey, incidenteDto, Duration.ofDays(1));
        }
        return incidenteDto;
    }

    @Override
    public List<IncidenteDto> findByNombreRuta(String nombreRuta) {
        String cacheKey = CACHE_KEY_PREFIX + nombreRuta;
        Integer consultasPrevias = rutaConsultaContador.getOrDefault(nombreRuta, 0);

        Duration ttl = Duration.ofHours(1);
        if (consultasPrevias >= CONSULTA_MAX) {
            ttl = Duration.ofMinutes(10);
            redisTemplate.expire(cacheKey, ttl);
        }

        // Verificar si los incidentes ya están cacheados
        List<IncidenteDto> incidentesCacheados = (List<IncidenteDto>) redisTemplate.opsForValue().get(cacheKey);
        if (incidentesCacheados != null) {
            rutaConsultaContador.put(nombreRuta, consultasPrevias + 1);
            log.info("📌 Cache hit para ruta: {}", nombreRuta);
            return incidentesCacheados;
        }

        log.info("🔍 Cache miss para ruta: {}", nombreRuta);

        List<Incidente> incidenteList = incidenteRepository.findIncidentesByRutaNombre(nombreRuta);
        List<IncidenteDto> incidenteDtoList = new ArrayList<>();
        List<RutaDto> interseccionesDtoList = new ArrayList<>();

        for(Incidente incidente : incidenteList) {
            IncidenteDto incidenteDto = new IncidenteDto();

            Ruta ruta = incidente.getRuta();
            RutaDto rutaDto = new RutaDto();

            rutaDto.setId(ruta.getId());
            rutaDto.setNombre(ruta.getNombre());
            rutaDto.setOrigen(ruta.getOrigen());
            rutaDto.setDestino(ruta.getDestino());
            rutaDto.setDistancia(ruta.getDistancia());

            if (ruta.getIntersecciones() != null) {
                for (Ruta interseccion : ruta.getIntersecciones()) {
                    RutaDto interseccionDto = new RutaDto();

                    interseccionDto.setId(interseccion.getId());
                    interseccionDto.setNombre(interseccion.getNombre());
                    interseccionDto.setOrigen(interseccion.getOrigen());
                    interseccionDto.setDestino(interseccion.getDestino());
                    interseccionDto.setDistancia(interseccion.getDistancia());

                    interseccionesDtoList.add(interseccionDto);
                }
            }

            rutaDto.setIntersecciones(interseccionesDtoList);

            incidenteDto.setId(incidente.getId());
            incidenteDto.setTipoIncidente(incidente.getTipoIncidente());
            incidenteDto.setRuta(rutaDto);
            incidenteDto.setTimestamp(incidente.getTimestamp());
            incidenteDto.setKilometro(incidente.getKilometro());
            incidenteDto.setComentarios(incidente.getComentarios());

            incidenteDtoList.add(incidenteDto);
        }

        redisTemplate.opsForValue().set(cacheKey, incidenteDtoList, ttl);

        log.info("Ruta {} almacenada en caché por {} min. (Consulta #{})", nombreRuta, ttl.toMinutes(), consultasPrevias);

        rutaConsultaContador.put(nombreRuta, consultasPrevias + 1);

        return incidenteDtoList;
    }

    @Override
    public List<IncidenteDto> findByNombreRutaAndKilometro(String nombreRuta, double kilometro) {
        String cacheKey = "incidentes_" + nombreRuta + "_" + kilometro;

        // Intentamos obtener los datos del cache
        List<IncidenteDto> incidenteDtoList = (List<IncidenteDto>) redisTemplate.opsForValue().get(cacheKey);

        if (incidenteDtoList == null) {
            List<Incidente> incidenteList = incidenteRepository.findIncidentesByRutaNombreAndKilometroRange(nombreRuta, kilometro);

            incidenteDtoList = new ArrayList<>();
            List<RutaDto> interseccionesDtoList = new ArrayList<>();

            for(Incidente incidente : incidenteList) {
                IncidenteDto incidenteDto = new IncidenteDto();

                Ruta ruta = incidente.getRuta();
                RutaDto rutaDto = new RutaDto();

                rutaDto.setId(ruta.getId());
                rutaDto.setNombre(ruta.getNombre());
                rutaDto.setOrigen(ruta.getOrigen());
                rutaDto.setDestino(ruta.getDestino());
                rutaDto.setDistancia(ruta.getDistancia());

                if (ruta.getIntersecciones() != null) {
                    for (Ruta interseccion : ruta.getIntersecciones()) {
                        RutaDto interseccionDto = new RutaDto();

                        interseccionDto.setId(interseccion.getId());
                        interseccionDto.setNombre(interseccion.getNombre());
                        interseccionDto.setOrigen(interseccion.getOrigen());
                        interseccionDto.setDestino(interseccion.getDestino());
                        interseccionDto.setDistancia(interseccion.getDistancia());

                        interseccionesDtoList.add(interseccionDto);
                    }
                }

                rutaDto.setIntersecciones(interseccionesDtoList);

                incidenteDto.setId(incidente.getId());
                incidenteDto.setTipoIncidente(incidente.getTipoIncidente());
                incidenteDto.setRuta(rutaDto);
                incidenteDto.setTimestamp(incidente.getTimestamp());
                incidenteDto.setKilometro(incidente.getKilometro());
                incidenteDto.setComentarios(incidente.getComentarios());

                incidenteDtoList.add(incidenteDto);
            }

            // Cacheamos los resultados
            redisTemplate.opsForValue().set(cacheKey, incidenteDtoList, Duration.ofDays(1));
        }

        return incidenteDtoList;
    }

    @Override
    public List<ReporteTramoDto> obtenerReportePromedioGravedadPorRuta(String nombreRuta) {
        String cacheKey = "reporte_promedio_gravedad_" + nombreRuta;

        // 🔍 Intentar obtener los datos del cache
        List<ReporteTramoDto> reporteTramoDtoList = (List<ReporteTramoDto>) redisTemplate.opsForValue().get(cacheKey);

        // Si no encontramos los datos en caché, generamos el reporte
        if (reporteTramoDtoList == null) {
            reporteTramoDtoList = incidenteRepository.obtenerReportePromedioGravedadPorRuta(nombreRuta);

            redisTemplate.opsForValue().set(cacheKey, reporteTramoDtoList, Duration.ofDays(1));
        }

        return reporteTramoDtoList;
    }


    @Override
    public List<IncidenteDto> findByTipoIncidente(String tipoIncidente) {
        List<Incidente> incidenteList = incidenteRepository.findByTipoIncidente(tipoIncidente);
        List<IncidenteDto> incidenteDtoList = new ArrayList<>();
        List<RutaDto> interseccionesDtoList = new ArrayList<>();

        for(Incidente incidente : incidenteList) {
            IncidenteDto incidenteDto = new IncidenteDto();

            Ruta ruta = incidente.getRuta();
            RutaDto rutaDto = new RutaDto();

            rutaDto.setId(ruta.getId());
            rutaDto.setNombre(ruta.getNombre());
            rutaDto.setOrigen(ruta.getOrigen());
            rutaDto.setDestino(ruta.getDestino());
            rutaDto.setDistancia(ruta.getDistancia());

            if (ruta.getIntersecciones() != null) {
                for (Ruta interseccion : ruta.getIntersecciones()) {
                    RutaDto interseccionDto = new RutaDto();

                    interseccionDto.setId(interseccion.getId());
                    interseccionDto.setNombre(interseccion.getNombre());
                    interseccionDto.setOrigen(interseccion.getOrigen());
                    interseccionDto.setDestino(interseccion.getDestino());
                    interseccionDto.setDistancia(interseccion.getDistancia());

                    interseccionesDtoList.add(interseccionDto);
                }
            }

            rutaDto.setIntersecciones(interseccionesDtoList);

            incidenteDto.setId(incidente.getId());
            incidenteDto.setTipoIncidente(incidente.getTipoIncidente());
            incidenteDto.setRuta(rutaDto);
            incidenteDto.setTimestamp(incidente.getTimestamp());
            incidenteDto.setKilometro(incidente.getKilometro());
            incidenteDto.setComentarios(incidente.getComentarios());

            incidenteDtoList.add(incidenteDto);
        }

        return incidenteDtoList;
    }

    @Override
    public List<IncidenteDto> findByTipoPrioridad(int tipoPrioridad) {
        List<Incidente> incidenteList = incidenteRepository.findByTipoPrioridad(tipoPrioridad);

        List<IncidenteDto> incidenteDtoList = new ArrayList<>();
        List<RutaDto> interseccionesDtoList = new ArrayList<>();

        for(Incidente incidente : incidenteList) {
            IncidenteDto incidenteDto = new IncidenteDto();

            Ruta ruta = incidente.getRuta();
            RutaDto rutaDto = new RutaDto();

            rutaDto.setId(ruta.getId());
            rutaDto.setNombre(ruta.getNombre());
            rutaDto.setOrigen(ruta.getOrigen());
            rutaDto.setDestino(ruta.getDestino());
            rutaDto.setDistancia(ruta.getDistancia());

            if (ruta.getIntersecciones() != null) {
                for (Ruta interseccion : ruta.getIntersecciones()) {
                    RutaDto interseccionDto = new RutaDto();

                    interseccionDto.setId(interseccion.getId());
                    interseccionDto.setNombre(interseccion.getNombre());
                    interseccionDto.setOrigen(interseccion.getOrigen());
                    interseccionDto.setDestino(interseccion.getDestino());
                    interseccionDto.setDistancia(interseccion.getDistancia());

                    interseccionesDtoList.add(interseccionDto);
                }
            }

            rutaDto.setIntersecciones(interseccionesDtoList);

            incidenteDto.setId(incidente.getId());
            incidenteDto.setTipoIncidente(incidente.getTipoIncidente());
            incidenteDto.setRuta(rutaDto);
            incidenteDto.setTimestamp(incidente.getTimestamp());
            incidenteDto.setKilometro(incidente.getKilometro());
            incidenteDto.setComentarios(incidente.getComentarios());

            incidenteDtoList.add(incidenteDto);
        }

        return incidenteDtoList;
    }


    @Override
    public IncidenteDto save(IncidenteDto incidenteDto) {
        Incidente incidente = new Incidente();

        if (incidenteDto.getRuta() != null && incidenteDto.getRuta().getId() != null) {
            Ruta rutaCompleta = rutaRepository.findById(incidenteDto.getRuta().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "La Ruta con ID '" + incidenteDto.getRuta().getId() + "' no existe."
                    ));
            incidente.setRuta(rutaCompleta);
        }

        if (incidenteDto.getTipoIncidente() != null && incidenteDto.getTipoIncidente().getId() != null) {
            TipoIncidente tipoIncidente = tipoIncidenteRepository.findById(incidenteDto.getTipoIncidente().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El Tipo de Incidente con ID '" + incidenteDto.getTipoIncidente().getId() + "' no existe."
                    ));
            incidente.setTipoIncidente(tipoIncidente);
        }

        incidente.setKilometro(incidenteDto.getKilometro());
        incidente.setTimestamp(incidenteDto.getTimestamp());
        incidente.setComentarios(incidenteDto.getComentarios());

        incidente = incidenteRepository.save(incidente);

        // Invalida el cache correspondiente a la ruta asociada al incidente
        String cacheKey = CACHE_KEY_PREFIX + incidente.getRuta().getNombre();
        redisTemplate.delete(cacheKey);  // Elimina el cache


        String cacheKeyRutaKilometro = "incidentes_" + incidente.getRuta().getNombre() + "_" + incidente.getKilometro();
        redisTemplate.delete(cacheKeyRutaKilometro);  // Elimina el cache por ruta y km

        // Elimina el cache de todos los incidentes para actualizarlo
        redisTemplate.delete("allIncidentes");
        redisTemplate.delete("incidente_" + incidente.getId()); // Invalida el cache individual

        // Elimina el cache relacionado con el reporte de promedio de gravedad
        String cacheKeyReporteGravedad = "reporte_promedio_gravedad_" + incidente.getRuta().getNombre();
        redisTemplate.delete(cacheKeyReporteGravedad);  // Elimina el cache del reporte de promedio de gravedad

        RutaDto rutaDto = null;
        if (incidente.getRuta() != null) {
            rutaDto = new RutaDto(
                    incidente.getRuta().getId(),
                    incidente.getRuta().getNombre(),
                    incidente.getRuta().getOrigen(),
                    incidente.getRuta().getDestino(),
                    incidente.getRuta().getDistancia(),
                    null // Evitamos la recursión infinita
            );
        }

        IncidenteDto nuevoDto = new IncidenteDto(
                incidente.getId(),
                rutaDto,
                incidente.getTipoIncidente(),
                incidente.getKilometro(),
                incidente.getTimestamp(),
                incidente.getComentarios()
        );

        // Guardamos en cache el nuevo incidente
        redisTemplate.opsForValue().set("incidente_" + nuevoDto.getId(), nuevoDto, Duration.ofDays(1));

        return nuevoDto;
    }

    @Override
    public IncidenteDto update(IncidenteDto incidenteDto) {
        return null;
    }

    @Override
    public void deleteById(String id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidente con ID '" + id + "' no encontrado."));

        // Eliminar cache relacionado con la ruta del incidente
        String cacheKey = CACHE_KEY_PREFIX + incidente.getRuta().getNombre();
        redisTemplate.delete(cacheKey);  // Elimina el cache

        String cacheKeyRutaKilometro = "incidentes_" + incidente.getRuta().getNombre() + "_" + incidente.getKilometro();
        redisTemplate.delete(cacheKeyRutaKilometro); // Elimina cache de búsqueda por ruta y km

        // Eliminar cache del reporte de promedio de gravedad relacionado con la ruta
        String cacheKeyReporteGravedad = "reporte_promedio_gravedad_" + incidente.getRuta().getNombre();
        redisTemplate.delete(cacheKeyReporteGravedad);  // Elimina el cache del reporte de promedio de gravedad

        incidenteRepository.delete(incidente);

        // Elimina el cache de todos los incidentes
        redisTemplate.delete("allIncidentes");
        redisTemplate.delete("incidente_" + id);

        log.info("Incidente con ID '{}' eliminado correctamente.", id);
    }

}
