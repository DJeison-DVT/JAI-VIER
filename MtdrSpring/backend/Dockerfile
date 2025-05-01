FROM maven:3.8-openjdk-11 as builder

WORKDIR /app

# Copy the Spring Boot JAR file
COPY target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

# Create a directory for the wallet
RUN mkdir -p /app/wallet

# Set the environment variable for the DB wallet
ENV DB_WALLET_LOCATION=/app/wallet

# Expose the port your Spring Boot app is running on
EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "app.jar"]

