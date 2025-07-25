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

# Copy and build Service2-Results-PDF
COPY ../../Service2-Results-PDF ./Service2-Results-PDF
WORKDIR /app/Service2-Results-PDF
RUN mvn clean package -DskipTests

# ---------- Stage 2: Create lightweight runtime image ----------
FROM eclipse-temurin:17-jdk

# Set working directory for the runtime container
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/Service2-Results-PDF/target/*.jar app.jar

# Expose the gRPC port used by this service
EXPOSE 9092

# === CONFIGURATION ===
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/service2_db
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=29042002Sa

ENV AZURE_STORAGE_CONNECTION_STRING=DefaultEndpointsProtocol=https;AccountName=tfgpdfs;AccountKey=gIgXNNFNpeSREpjyDZzOiMvT2vZeC+7Um2u/jWdnXx3iVhkyRaePNGj5I+kR5S9kMMxL4bVfgck6+ASt/wrBUQ==;EndpointSuffix=core.windows.net

ENV SPRING_RABBITMQ_HOST=host.docker.internal

ENTRYPOINT ["java", "-jar", "app.jar"]
