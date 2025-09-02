# ---------- Stage 1: Build the application ----------
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Build shared-protos first (local Maven dependency)
COPY ../../shared-protos ./shared-protos
WORKDIR /app/shared-protos
RUN mvn clean install -DskipTests

# Build service1-backend
WORKDIR /app
COPY ../../service1-backend ./service1-backend
WORKDIR /app/service1-backend
RUN mvn clean package -DskipTests

# ---------- Stage 2: Lightweight runtime image ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/service1-backend/target/*.jar app.jar

# Expose Spring Boot HTTPS port
EXPOSE 9091

# === Environment variables: injected from Kubernetes Deployment ===
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""
ENV SPRING_RABBITMQ_HOST=""
ENV GRPC_CLIENT_SERVICIO2_ADDRESS=""
ENV GRPC_CLIENT_SERVICIO3_ADDRESS=""
ENV SERVER_SSL_ENABLED=true

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
