package brian.duran.demo_shark_ruta_incidentes.service.impl;

import brian.duran.demo_shark_ruta_incidentes.dto.RutaDto;
import brian.duran.demo_shark_ruta_incidentes.entity.Ruta;
import brian.duran.demo_shark_ruta_incidentes.repository.IRutaRepository;
import brian.duran.demo_shark_ruta_incidentes.service.IRutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RutaService implements IRutaService {
    @Autowired
    private IRutaRepository rutaRepository;

    @Override
    public List<RutaDto> findAll() {
        List<Ruta> rutaList = rutaRepository.findAll();
        List<RutaDto> rutaDtoList = new ArrayList<>();

        rutaList.forEach(ruta -> {
            RutaDto rutaDto = new RutaDto();
            rutaDto.setId(ruta.getId());
            rutaDto.setNombre(ruta.getNombre());
            rutaDto.setOrigen(ruta.getOrigen());
            rutaDto.setDestino(ruta.getDestino());
            rutaDto.setDistancia(ruta.getDistancia());

            // Convertir intersecciones de Ruta a RutaDto manualmente para evitar recursión infinita
            if (ruta.getIntersecciones() != null) {
                List<RutaDto> interseccionesDto = new ArrayList<>();
                for (Ruta interseccion : ruta.getIntersecciones()) {
                    RutaDto interseccionDto = new RutaDto();
                    if(interseccion != null) {
                        Optional<Ruta> existRuta = rutaRepository.findById(interseccion.getId());
                        if(existRuta.isPresent()) {
                            interseccionDto.setId(interseccion.getId());
                            interseccionDto.setNombre(interseccion.getNombre());
                            interseccionDto.setOrigen(interseccion.getOrigen());
                            interseccionDto.setDestino(interseccion.getDestino());
                            interseccionDto.setDistancia(interseccion.getDistancia());
                            interseccionesDto.add(interseccionDto);
                        }
                    }else {
                        interseccionesDto.add(null);
                    }
                }
                if (interseccionesDto.isEmpty()){
                    rutaDto.setIntersecciones(null);
                }else {
                    rutaDto.setIntersecciones(interseccionesDto);
                }

            } else {
                System.out.println("Ruta {} sin intersecciones" + ruta.getNombre());
            }

            rutaDtoList.add(rutaDto);
        });

        return rutaDtoList;
    }

    @Override
    public RutaDto findById(String id) {
        Ruta existRuta = rutaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La Ruta con ID '" + id + "' no se encuentra registrada."
                ));

        RutaDto rutaDto = new RutaDto();
        rutaDto.setId(existRuta.getId());
        rutaDto.setNombre(existRuta.getNombre());
        rutaDto.setOrigen(existRuta.getOrigen());
        rutaDto.setDestino(existRuta.getDestino());
        rutaDto.setDistancia(existRuta.getDistancia());

        List<RutaDto> interseccionesDto = new ArrayList<>();
        if(existRuta.getIntersecciones() != null) {
            for (Ruta interseccion : existRuta.getIntersecciones()) {
                RutaDto interseccionDto = new RutaDto();
                interseccionDto.setId(interseccion.getId());
                interseccionDto.setNombre(interseccion.getNombre());
                interseccionDto.setOrigen(interseccion.getOrigen());
                interseccionDto.setDestino(interseccion.getDestino());
                interseccionDto.setDistancia(interseccion.getDistancia());
                interseccionesDto.add(interseccionDto);
            }
        }

        rutaDto.setIntersecciones(interseccionesDto);
        return rutaDto;
    }

    @Cacheable(value = "cache1", key = "#nombre")
    @Override
    public RutaDto findByName(String nombre) {
        Ruta existRuta = rutaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La Ruta con nombre '" + nombre + "' no se encuentra registrada."
                ));

        RutaDto rutaDto = new RutaDto();
        rutaDto.setId(existRuta.getId());
        rutaDto.setNombre(existRuta.getNombre());
        rutaDto.setOrigen(existRuta.getOrigen());
        rutaDto.setDestino(existRuta.getDestino());
        rutaDto.setDistancia(existRuta.getDistancia());

        List<RutaDto> interseccionesDto = new ArrayList<>();

        if(existRuta.getIntersecciones() != null) {
            for (Ruta interseccion : existRuta.getIntersecciones()) {
                RutaDto interseccionDto = new RutaDto();
                interseccionDto.setId(interseccion.getId());
                interseccionDto.setNombre(interseccion.getNombre());
                interseccionDto.setOrigen(interseccion.getOrigen());
                interseccionDto.setDestino(interseccion.getDestino());
                interseccionDto.setDistancia(interseccion.getDistancia());
                interseccionesDto.add(interseccionDto);
            }
        }


        rutaDto.setIntersecciones(interseccionesDto);
        return rutaDto;
    }

    @Override
    public RutaDto save(RutaDto rutaDto) {
        if (rutaDto.getId() != null) {
            Optional<Ruta> existRuta = rutaRepository.findById(rutaDto.getId());
            if (existRuta.isPresent()) {
                throw new IllegalArgumentException("La Ruta con ID '" + rutaDto.getId() + "' ya se encuentra registrada.");
            }
        }

        Ruta ruta = new Ruta();
        ruta.setNombre(rutaDto.getNombre());
        ruta.setOrigen(rutaDto.getOrigen());
        ruta.setDestino(rutaDto.getDestino());
        ruta.setDistancia(rutaDto.getDistancia());

        if (rutaDto.getIntersecciones() != null && !rutaDto.getIntersecciones().isEmpty()) {
            List<Ruta> intersecciones = rutaDto.getIntersecciones().stream()
                    .map(interseccionDto -> rutaRepository.findById(interseccionDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Intersección con ID '" + interseccionDto.getId() + "' no encontrada.")))
                    .collect(Collectors.toList());

            ruta.setIntersecciones(intersecciones);
        }

        ruta = rutaRepository.save(ruta);

        RutaDto rutaDtoResult = new RutaDto();
        rutaDtoResult.setId(ruta.getId());
        rutaDtoResult.setNombre(ruta.getNombre());
        rutaDtoResult.setOrigen(ruta.getOrigen());
        rutaDtoResult.setDestino(ruta.getDestino());
        rutaDtoResult.setDistancia(ruta.getDistancia());

        if (ruta.getIntersecciones() != null) {
            List<RutaDto> interseccionesDto = new ArrayList<>();
            for (Ruta interseccion : ruta.getIntersecciones()) {
                RutaDto interseccionDto = new RutaDto();
                interseccionDto.setId(interseccion.getId());
                interseccionDto.setNombre(interseccion.getNombre());
                interseccionDto.setOrigen(interseccion.getOrigen());
                interseccionDto.setDestino(interseccion.getDestino());
                interseccionDto.setDistancia(interseccion.getDistancia());
                interseccionesDto.add(interseccionDto);
            }

            rutaDtoResult.setIntersecciones(interseccionesDto);
        } else {
            System.out.println("Se registra ruta sin intersecciones");
        }


        return rutaDtoResult;
    }

    @Override
    public RutaDto update(RutaDto rutaDto) {

        Optional<Ruta> existRuta = rutaRepository.findById(rutaDto.getId());
        if (existRuta.isEmpty()) {
            throw new IllegalArgumentException("La Ruta con ID '" + rutaDto.getId() + "' no se encuentra registrada en base de datos.");
        }

        Ruta ruta = new Ruta();
        ruta.setId(rutaDto.getId());
        ruta.setNombre(rutaDto.getNombre());
        ruta.setOrigen(rutaDto.getOrigen());
        ruta.setDestino(rutaDto.getDestino());
        ruta.setDistancia(rutaDto.getDistancia());

        if (rutaDto.getIntersecciones() != null && !rutaDto.getIntersecciones().isEmpty()) {
            List<Ruta> intersecciones = rutaDto.getIntersecciones().stream()
                    .map(interseccionDto -> rutaRepository.findById(interseccionDto.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Intersección con ID '" + interseccionDto.getId() + "' no encontrada.")))
                    .collect(Collectors.toList());

            ruta.setIntersecciones(intersecciones);
        }

        ruta = rutaRepository.save(ruta);

        RutaDto rutaDtoResult = new RutaDto();
        rutaDtoResult.setId(ruta.getId());
        rutaDtoResult.setNombre(ruta.getNombre());
        rutaDtoResult.setOrigen(ruta.getOrigen());
        rutaDtoResult.setDestino(ruta.getDestino());
        rutaDtoResult.setDistancia(ruta.getDistancia());

       List<RutaDto> interseccionesDto = new ArrayList<>();
        if (ruta.getIntersecciones() != null) {
            for (Ruta interseccion : ruta.getIntersecciones()) {
                RutaDto interseccionDto = new RutaDto();
                interseccionDto.setId(interseccion.getId());
                interseccionDto.setNombre(interseccion.getNombre());
                interseccionDto.setOrigen(interseccion.getOrigen());
                interseccionDto.setDestino(interseccion.getDestino());
                interseccionDto.setDistancia(interseccion.getDistancia());
                interseccionesDto.add(interseccionDto);
            }
        }

        rutaDtoResult.setIntersecciones(interseccionesDto);

        return rutaDtoResult;
    }

    @Override
    public void deleteById(String id) {
        Optional<Ruta> existRuta = rutaRepository.findById(id);

        if (existRuta.isEmpty()) {
            throw new IllegalArgumentException("La Ruta con ID '" + id + "' no existe en la base de datos.");
        }

        if(existRuta.get().getIntersecciones() != null) {
            // Si tiene intersecciones, lanzar una excepción
            if (!existRuta.get().getIntersecciones().isEmpty()) {
                throw new IllegalArgumentException("Para eliminar, la ruta no debe poseer intersecciones.");
            }
        }

        rutaRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String nombre) {
        Optional<Ruta> existRuta = rutaRepository.findByNombre(nombre);

        if (existRuta.isEmpty()) {
            throw new IllegalArgumentException("La Ruta '" + nombre + "' no existe en la base de datos.");
        }

        if(existRuta.get().getIntersecciones() != null) {
            // Si tiene intersecciones, lanzar una excepción
            if (!existRuta.get().getIntersecciones().isEmpty()) {
                throw new IllegalArgumentException("Para eliminar, la ruta no debe poseer intersecciones.");
            }
        }

        rutaRepository.deleteByNombre(nombre);
    }
}
