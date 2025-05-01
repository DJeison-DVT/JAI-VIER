# -------- Build stage --------
FROM maven:3.8-openjdk-11 AS builder

WORKDIR /app
COPY ./MtdrSpring/backend/pom.xml .
COPY ./MtdrSpring/backend/src ./src

RUN mvn clean package -DskipTests

# -------- Runtime stage --------
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=builder /app/target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

# Wallet folder expected to be mounted at runtime (via K8s Secret)
ENV DB_WALLET_LOCATION=/app/wallet

# Create the wallet dir so mount path exists
RUN mkdir -p /app/wallet

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
