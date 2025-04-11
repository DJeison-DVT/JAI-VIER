FROM maven:3.8.6-openjdk-11-slim AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY ./pom.xml .
COPY ./src ./src

# Construir la aplicación y omitir pruebas
RUN mvn clean package -DskipTests

# Segunda etapa: imagen de ejecución
FROM openjdk:11-jre-slim

WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Variables de entorno por defecto (se pueden sobrescribir al ejecutar el contenedor)
ENV SERVER_PORT=8081

# Exponer el puerto de la aplicación
EXPOSE ${SERVER_PORT}

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]