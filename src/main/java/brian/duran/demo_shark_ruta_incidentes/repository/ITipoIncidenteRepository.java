package brian.duran.demo_shark_ruta_incidentes.repository;

import brian.duran.demo_shark_ruta_incidentes.entity.TipoIncidente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITipoIncidenteRepository extends MongoRepository<TipoIncidente, String> {
    List<TipoIncidente> findByTipoIncidente(String tipoIncidente);
    List<TipoIncidente> findByTipoPrioridad(int tipoPrioridad);
}
