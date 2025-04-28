FROM maven:3.8-openjdk-11 as builder

# Directorio de trabajo
WORKDIR /app

# Copiar pom.xml para descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN mvn package -DskipTests

# Imagen final
FROM openjdk:11-jre-slim

# Crear directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de construcción
COPY --from=builder /app/target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

# Crear directorio para la wallet
RUN mkdir -p /app/wallet

# Copiar la wallet desde la máquina local al contenedor
COPY src/main/resources/wallet /app/wallet

# Exponer el puerto
EXPOSE 8081

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]