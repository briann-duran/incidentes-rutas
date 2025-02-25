# Imagen de Maven para compilar el proyecto
FROM maven:3.9.7-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar el pom.xml y el código fuente
COPY pom.xml .
COPY src ./src

# Descargar las dependencias y compilar el .jar
RUN mvn clean package -DskipTests

# Etapa de ejecución con Java 17
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Variables de entorno para MongoDB y Redis
#ENV SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/incidentes_db
#ENV SPRING_REDIS_HOST=redis
#ENV SPRING_REDIS_PORT=6379

# Copiar el archivo JAR generado en la etapa de build
COPY --from=build /app/target/demo-shark-ruta-incidentes-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que utilizará la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
