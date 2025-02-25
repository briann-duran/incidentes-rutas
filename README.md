# Incidentes-Rutas

## Descripción

**Incidentes-Rutas** es una aplicación que permite consultar y gestionar los incidentes ocurridos en distintas rutas. Los usuarios pueden obtener información sobre los incidentes filtrados por nombre de ruta, tipo de incidente y su prioridad. Además, la aplicación soporta operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para la gestión de los incidentes, y genera reportes visuales para facilitar el análisis de los datos.

### Funcionalidades

- **Consulta de Incidentes:** Obtener los incidentes ocurridos en una ruta específica.
- **Filtros Avanzados:** Filtrar los incidentes por tipo de incidente o nivel de prioridad.
- **Generación de Reportes:** Crear reportes de incidentes dentro de un tramo específico de una ruta, desde un kilómetro inicial hasta 100 kilómetros, ordenados por gravedad.
- **CRUD:** Crear, actualizar y eliminar incidentes según sea necesario.

### Tecnologías Implementadas

- **Base de Datos:** MongoDB para el almacenamiento de los incidentes.
- **Cache en Memoria:** Redis, utilizado para almacenar en cache las consultas frecuentes y mejorar el rendimiento.
- **Optimización de Consultas:** Utilización de consultas **Aggregation** en MongoDB para agilizar las búsquedas y obtener relaciones entre incidentes, rutas y tipos de incidentes.
  
### Estrategias de Cache

- Si una ruta recibe más de 5 consultas, todas las consultas posteriores se cachean por un periodo de 10 minutos.
- Las operaciones de **ALTA** o **BAJA** de incidentes invalidan el cache, asegurando que los datos en memoria estén siempre actualizados.

## Ejemplo de Incidentes Iniciales

Al iniciar el servicio, la aplicación carga algunos incidentes de ejemplo. A continuación, se muestra cómo se estructura un incidente inicial:

```json
[
  {
    "id": "67bd7bf1bdd82c078bd93488",
    "ruta": {
      "id": "67bd7bf1bdd82c078bd9347e",
      "nombre": "Ruta del Atlántico",
      "origen": "Retiro",
      "destino": "Mar del Plata",
      "distancia": 400.0,
      "intersecciones": [
        {
          "id": "67bd7bf1bdd82c078bd93480",
          "nombre": "Ruta Patagónica",
          "origen": "Bahía Blanca",
          "destino": "Puerto Madryn",
          "distancia": 650.0,
          "intersecciones": null
        }
      ]
    },
    "tipoIncidente": {
      "id": "67bd7bf1bdd82c078bd93483",
      "tipoIncidente": "Accidente",
      "tipoPrioridad": 1
    },
    "kilometro": 115.7,
    "timestamp": "2025-02-25T05:14:41.149",
    "comentarios": "Accidente múltiple en la autopista."
  },
  ...
]
```

###Endpoints de la API

La aplicación expone una serie de endpoints para interactuar con el backend. A continuación, se muestran los endpoints agrupados por sus respectivos controladores. Para más detalles y para interactuar con los endpoints, accede a la documentación completa en Swagger UI mediante la siguiente URL: http://localhost:8080/doc/swagger-ui/index.html.

** Tipo Incidente Controller **
```bash
GET /api/v1/tipo_incidentes: Obtener todos los tipos de incidentes.
```
```bash
PUT /api/v1/tipo_incidentes: Actualizar un tipo de incidente.
```
POST /api/v1/tipo_incidentes: Crear un nuevo tipo de incidente.
GET /api/v1/tipo_incidentes/{id}: Obtener un tipo de incidente específico.
DELETE /api/v1/tipo_incidentes/{id}: Eliminar un tipo de incidente.
GET /api/v1/tipo_incidentes/tipo: Obtener los tipos de incidentes.
GET /api/v1/tipo_incidentes/prioridad: Obtener las prioridades de los incidentes.
Ruta Controller
GET /api/v1/rutas: Obtener todas las rutas.
PUT /api/v1/rutas: Actualizar una ruta.
POST /api/v1/rutas: Crear una nueva ruta.
DELETE /api/v1/rutas: Eliminar una ruta.
GET /api/v1/rutas/{name}: Obtener una ruta por nombre.
GET /api/v1/rutas/id: Obtener una ruta por ID.
DELETE /api/v1/rutas/{nombre}: Eliminar una ruta por nombre.
Incidente Controller
GET /api/v1/incidentes: Obtener todos los incidentes.
POST /api/v1/incidentes: Crear un nuevo incidente.
GET /api/v1/incidentes/{id}: Obtener un incidente específico.
DELETE /api/v1/incidentes/{id}: Eliminar un incidente.
GET /api/v1/incidentes/tipoPrioridad: Obtener las prioridades de los incidentes.
GET /api/v1/incidentes/tipoIncidente: Obtener los tipos de incidentes.
GET /api/v1/incidentes/rutas: Obtener las rutas relacionadas con los incidentes.
GET /api/v1/incidentes/rutas/kilometro: Obtener incidentes por kilómetro de ruta.
GET /api/v1/incidentes/reporte: Obtener un reporte de los incidentes.

### Captura de Swagger UI
Para interactuar con los endpoints, puedes acceder a la documentación completa de la API a través de Swagger UI. Aquí tienes una captura de cómo se muestra:
