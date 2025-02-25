# Incidentes-Rutas

## Descripción

La aplicación **Incidentes-Rutas** está diseñada para consultar y gestionar los incidentes ocurridos en diferentes rutas. Permite obtener información de los incidentes basados en el nombre de una ruta en particular, el tipo de incidente o la prioridad del mismo. Además, cuenta con funcionalidades CRUD (Crear, Leer, Actualizar, Eliminar) para gestionar los datos y la opción de generar reportes visuales de los incidentes de una ruta.

### Funcionalidades

- **Consulta de Incidentes:** Permite obtener los incidentes que han ocurrido en una ruta específica.
- **Filtros:** Se pueden filtrar los incidentes por tipo de incidente o prioridad.
- **Generación de Reportes:** Se pueden generar reportes de los incidentes ocurridos en un tramo de ruta, especificando desde un kilómetro inicial hasta 100 kilómetros. Los resultados se muestran en tramos de 100 km, ordenados por gravedad.
- **CRUD:** Es posible realizar acciones para crear, actualizar o eliminar incidentes.
  
### Almacenamiento y Optimización

- **Base de Datos:** Los incidentes son almacenados en **MongoDB**.
- **Cache en Memoria:** Utiliza **Redis** para almacenar en cache las consultas realizadas, optimizando el tiempo de respuesta. 
- **Gestión de Cache:** 
  - Si una ruta recibe más de 5 consultas, todas las consultas futuras se cachean por 10 minutos.
  - Si se realiza una acción de **ALTA** o **BAJA** de un incidente, los datos en cache se invalidan para actualizar la información.
- **Consultas Agilizadas:** Se utilizaron consultas **Aggregation** en MongoDB para agilizar las búsquedas de incidentes y sus relaciones con otros documentos como Rutas y Tipos de Incidentes.

## Ejemplo de Incidentes Iniciales

A continuación, te mostramos algunos de los incidentes iniciales cargados en la aplicación al arrancar el servicio:

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
