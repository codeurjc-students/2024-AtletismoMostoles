# 🧩 Service2 - Resultados y PDFs

Este microservicio forma parte del sistema distribuido de un club de atletismo. Su función principal es **gestionar los resultados de atletas y eventos**, así como **generar y servir reportes en PDF** de dichos resultados bajo demanda.

---

## 🚀 Funcionalidad principal

- 📊 **Gestión de resultados**:
    - Guardar nuevos resultados asociados a un atleta y evento.
    - Consultar resultados por atleta.
    - Consultar resultados por evento.

- 📄 **Generación de PDFs**:
    - Recibe solicitudes desde el frontend vía RabbitMQ.
    - Genera un PDF simulado con los resultados de un atleta o evento.
    - Almacena el PDF (simulado) y su historial.
    - Envia una confirmación a través de una segunda cola.

- 🧠 **Exposición de datos vía gRPC**:
    - Servicio gRPC que permite al Service1 consultar resultados y PDFs.

---

## 🗂️ Estructura del proyecto

```bash
src/main/java/com/example/service2/
├── config/                 # Configuración de RabbitMQ
├── dto/                   # DTOs para mensajes JSON
├── entities/              # Entidades JPA: Result y PdfRequest
├── grpc/                  # Servicio gRPC ResultadoServiceGrpcImpl
├── listeners/             # RabbitMQ listener para solicitudes de PDFs
├── messaging/             # Publicación en cola de confirmación
├── repositories/          # Interfaces JPA
├── services/              # Lógica de negocio desacoplada
└── Service2Application.java
