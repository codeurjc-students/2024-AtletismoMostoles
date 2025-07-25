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

# Copy and build Service3-Events
COPY ../../Service3-Events ./Service3-Events
WORKDIR /app/Service3-Events
RUN mvn clean package -DskipTests

# ---------- Stage 2: Create lightweight runtime image ----------
FROM eclipse-temurin:17-jdk

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/Service3-Events/target/*.jar app.jar

# Expose the gRPC port used by this service
EXPOSE 9093

# === CONFIGURATION ===
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/service3_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=29042002Sa

ENV SPRING_RABBITMQ_HOST=host.docker.internal

ENTRYPOINT ["java", "-jar", "app.jar"]
