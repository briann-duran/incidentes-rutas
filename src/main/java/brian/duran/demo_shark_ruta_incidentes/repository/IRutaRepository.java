package brian.duran.demo_shark_ruta_incidentes.repository;

import brian.duran.demo_shark_ruta_incidentes.entity.Ruta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRutaRepository extends MongoRepository<Ruta, String> {
    Optional<Ruta> findByNombre(String nombre);
    void deleteByNombre(String nombre);
}
