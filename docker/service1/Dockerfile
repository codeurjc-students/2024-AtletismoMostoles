# ---------- Stage 1: Build the application ----------
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Set working directory for the build process
WORKDIR /app

# Copy and build shared-protos first (local Maven dependency)
COPY ../../shared-protos ./shared-protos
WORKDIR /app/shared-protos
RUN mvn clean install -DskipTests

# Move back to main build context
WORKDIR /app

# Copy and build service1-backend
COPY ../../service1-backend ./service1-backend
WORKDIR /app/service1-backend
RUN mvn clean package -DskipTests

# ---------- Stage 2: Create lightweight runtime image ----------
FROM eclipse-temurin:17-jdk

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/service1-backend/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 9091

# === CONFIGURATION ===
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/service1_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=29042002Sa

ENV SPRING_RABBITMQ_HOST=host.docker.internal
ENV GRPC_CLIENT_SERVICIO2_ADDRESS=host.docker.internal:9092
ENV GRPC_CLIENT_SERVICIO3_ADDRESS=host.docker.internal:9093
ENV SERVER_SSL_ENABLED=true


# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
