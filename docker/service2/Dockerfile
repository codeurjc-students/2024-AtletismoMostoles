# ---------- Stage 1: Build the application ----------
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Build shared-protos first (local Maven dependency)
COPY ../../shared-protos ./shared-protos
WORKDIR /app/shared-protos
RUN mvn clean install -DskipTests

# Build Service2-Results-PDF
WORKDIR /app
COPY ../../Service2-Results-PDF ./Service2-Results-PDF
WORKDIR /app/Service2-Results-PDF
RUN mvn clean package -DskipTests

# ---------- Stage 2: Lightweight runtime image ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/Service2-Results-PDF/target/*.jar app.jar

# Expose gRPC port
EXPOSE 9092

# === Environment variables: injected from Kubernetes Deployment ===
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""
ENV SPRING_RABBITMQ_HOST=""
ENV AZURE_STORAGE_CONNECTION_STRING=""

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
