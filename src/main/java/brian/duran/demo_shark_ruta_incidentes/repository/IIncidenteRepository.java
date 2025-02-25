package brian.duran.demo_shark_ruta_incidentes.repository;

import brian.duran.demo_shark_ruta_incidentes.dto.ReporteTramoDto;
import brian.duran.demo_shark_ruta_incidentes.entity.Incidente;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IIncidenteRepository extends MongoRepository<Incidente, String> {
    @Aggregation({
            "{ $lookup: { from: 'RUTAS', localField: 'ruta.$id', foreignField: '_id', as: 'ruta_info' } }",
            "{ $unwind: '$ruta_info' }",
            "{ $lookup: { from: 'TIPO_INCIDENTES', localField: 'tipoIncidente.$id', foreignField: '_id', as: 'tipo_info' } }",
            "{ $unwind: '$tipo_info' }",
            "{ $match: { 'ruta_info.nombre': ?0 } }",
            "{ $lookup: { from: 'RUTAS', localField: 'ruta_info.intersecciones.$id', foreignField: '_id', as: 'intersecciones_info' } }",  // Obtener las intersecciones de la ruta
            "{ $unwind: { path: '$intersecciones_info', preserveNullAndEmptyArrays: true } }",  // Desenrollar intersecciones y permitir que sea null si no hay intersección
            "{ $project: { " +
                    "_id: 1, " +
                    "kilometro: 1, " +
                    "timestamp: 1, " +
                    "comentarios: 1, " +
                    "ruta: { " +
                    "_id: '$ruta_info._id', " +
                    "nombre: '$ruta_info.nombre', " +
                    "origen: '$ruta_info.origen', " +
                    "destino: '$ruta_info.destino', " +
                    "distancia: '$ruta_info.distancia', " +
                    "intersecciones: [" +
                    "{ _id: '$intersecciones_info._id', nombre: '$intersecciones_info.nombre', origen: '$intersecciones_info.origen', destino: '$intersecciones_info.destino', distancia: '$intersecciones_info.distancia' }" +
                    "]" +
                    "}, " +
                    "tipoIncidente: { " +
                    "_id: '$tipo_info._id', " +
                    "tipoIncidente: '$tipo_info.tipoIncidente', " +
                    "tipoPrioridad: '$tipo_info.tipoPrioridad' " +
                    "}" +
                    "} }"
    })
    List<Incidente> findIncidentesByRutaNombre(String nombreRuta);


    @Aggregation(pipeline = {
            "{ $lookup: { from: 'RUTAS', localField: 'ruta.$id', foreignField: '_id', as: 'ruta_info' } }",
            "{ $unwind: '$ruta_info' }",
            "{ $addFields: { maxKilometro: { $add: ['$kilometro', 100] } } }", // Calcula el máximo kilometraje
            "{ $match: { 'ruta_info.nombre': ?0, $expr: { $and: [ { $gte: ['$kilometro', ?1] }, { $lte: ['$kilometro', '$maxKilometro'] } ] } } } }", // Utiliza el maxKilometro calculado
            "{ $lookup: { from: 'TIPO_INCIDENTES', localField: 'tipoIncidente.$id', foreignField: '_id', as: 'tipo_info' } }",
            "{ $unwind: '$tipo_info' }",
            "{ $lookup: { from: 'RUTAS', localField: 'ruta_info.intersecciones.$id', foreignField: '_id', as: 'intersecciones_info' } }",
            "{ $unwind: { path: '$intersecciones_info', preserveNullAndEmptyArrays: true } }",
            "{ $project: { _id: 1, kilometro: 1, timestamp: 1, comentarios: 1, ruta: { _id: '$ruta_info._id', nombre: '$ruta_info.nombre', origen: '$ruta_info.origen', destino: '$ruta_info.destino', distancia: '$ruta_info.distancia', intersecciones: [ { _id: '$intersecciones_info._id', nombre: '$intersecciones_info.nombre', origen: '$intersecciones_info.origen', destino: '$intersecciones_info.destino', distancia: '$intersecciones_info.distancia' } ] }, tipoIncidente: { _id: '$tipo_info._id', tipoIncidente: '$tipo_info.tipoIncidente', tipoPrioridad: '$tipo_info.tipoPrioridad' } } }"
    })
    List<Incidente> findIncidentesByRutaNombreAndKilometroRange(String nombreRuta, double kilometro);

    @Aggregation(pipeline = {
            "{ '$lookup': { 'from': 'RUTAS', 'localField': 'ruta.$id', 'foreignField': '_id', 'as': 'ruta_info' } }",
            "{ '$unwind': '$ruta_info' }",
            "{ '$match': { 'ruta_info.nombre': ?0 } }",
            "{ '$lookup': { 'from': 'TIPO_INCIDENTES', 'localField': 'tipoIncidente.$id', 'foreignField': '_id', 'as': 'tipo_info' } }",
            "{ '$unwind': '$tipo_info' }",
            "{ '$addFields': { " +
                    "'tramo': { '$multiply': [{ '$toInt': { '$floor': { '$divide': ['$kilometro', 100] } } }, 100] } } " +
                    "} }",
            "{ '$group': { " +
                    "'_id': '$tramo', " +
                    "'gravedadPromedio': { '$avg': '$tipo_info.tipoPrioridad' }, " +
                    "'nombreRuta': { '$first': '$ruta_info.nombre' } " +
                    "} }",
            "{ '$project': { " +
                    "'_id': 0, " +
                    "'tramo': '$_id', " +
                    "'gravedadPromedio': 1, " +
                    "'nombreRuta': 1 " +
                    "} }",
            "{ '$sort': { 'gravedadPromedio': -1 } }"
    })
    List<ReporteTramoDto> obtenerReportePromedioGravedadPorRuta(String nombreRuta);

    @Aggregation({
            "{ $lookup: { from: 'RUTAS', localField: 'ruta.$id', foreignField: '_id', as: 'ruta_info' } }",
            "{ $unwind: '$ruta_info' }",
            "{ $lookup: { from: 'TIPO_INCIDENTES', localField: 'tipoIncidente.$id', foreignField: '_id', as: 'tipo_info' } }",
            "{ $unwind: '$tipo_info' }",
            "{ $match: { 'tipo_info.tipoIncidente': ?0 } }",  // Filtrar por el tipo de incidente
            "{ $lookup: { from: 'RUTAS', localField: 'ruta_info.intersecciones.$id', foreignField: '_id', as: 'intersecciones_info' } }",
            "{ $unwind: { path: '$intersecciones_info', preserveNullAndEmptyArrays: true } }",
            "{ $project: { " +
                    "_id: 1, " +
                    "kilometro: 1, " +
                    "timestamp: 1, " +
                    "comentarios: 1, " +
                    "ruta: { " +
                    "_id: '$ruta_info._id', " +
                    "nombre: '$ruta_info.nombre', " +
                    "origen: '$ruta_info.origen', " +
                    "destino: '$ruta_info.destino', " +
                    "distancia: '$ruta_info.distancia', " +
                    "intersecciones: [" +
                    "{ _id: '$intersecciones_info._id', nombre: '$intersecciones_info.nombre', origen: '$intersecciones_info.origen', destino: '$intersecciones_info.destino', distancia: '$intersecciones_info.distancia' }" +
                    "]" +
                    "}, " +
                    "tipoIncidente: { " +
                    "_id: '$tipo_info._id', " +
                    "tipoIncidente: '$tipo_info.tipoIncidente', " +
                    "tipoPrioridad: '$tipo_info.tipoPrioridad' " +
                    "}" +
                    "} }"
    })
    List<Incidente> findByTipoIncidente(String tipoIncidente);


    @Aggregation({
            "{ $lookup: { from: 'RUTAS', localField: 'ruta.$id', foreignField: '_id', as: 'ruta_info' } }",
            "{ $unwind: '$ruta_info' }",
            "{ $lookup: { from: 'TIPO_INCIDENTES', localField: 'tipoIncidente.$id', foreignField: '_id', as: 'tipo_info' } }",
            "{ $unwind: '$tipo_info' }",
            "{ $match: { 'tipo_info.tipoPrioridad': ?0 } }",  // Filtrar por tipo de prioridad
            "{ $lookup: { from: 'RUTAS', localField: 'ruta_info.intersecciones.$id', foreignField: '_id', as: 'intersecciones_info' } }",
            "{ $unwind: { path: '$intersecciones_info', preserveNullAndEmptyArrays: true } }",
            "{ $project: { " +
                    "_id: 1, " +
                    "kilometro: 1, " +
                    "timestamp: 1, " +
                    "comentarios: 1, " +
                    "ruta: { " +
                    "_id: '$ruta_info._id', " +
                    "nombre: '$ruta_info.nombre', " +
                    "origen: '$ruta_info.origen', " +
                    "destino: '$ruta_info.destino', " +
                    "distancia: '$ruta_info.distancia', " +
                    "intersecciones: [" +
                    "{ _id: '$intersecciones_info._id', nombre: '$intersecciones_info.nombre', origen: '$intersecciones_info.origen', destino: '$intersecciones_info.destino', distancia: '$intersecciones_info.distancia' }" +
                    "]" +
                    "}, " +
                    "tipoIncidente: { " +
                    "_id: '$tipo_info._id', " +
                    "tipoIncidente: '$tipo_info.tipoIncidente', " +
                    "tipoPrioridad: '$tipo_info.tipoPrioridad' " +
                    "}" +
                    "} }"
    })
    List<Incidente> findByTipoPrioridad(int tipoPrioridad);
}
