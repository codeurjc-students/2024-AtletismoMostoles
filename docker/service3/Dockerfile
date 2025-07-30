# ---------- Stage 1: Build the application ----------
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Build shared-protos first (local Maven dependency)
COPY ../../shared-protos ./shared-protos
WORKDIR /app/shared-protos
RUN mvn clean install -DskipTests

# Build Service3-Events
WORKDIR /app
COPY ../../Service3-Events ./Service3-Events
WORKDIR /app/Service3-Events
RUN mvn clean package -DskipTests

# ---------- Stage 2: Lightweight runtime image ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/Service3-Events/target/*.jar app.jar

# Expose gRPC port
EXPOSE 9094

# === Environment variables: injected from Kubernetes Deployment ===
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""
ENV SPRING_RABBITMQ_HOST=""

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
