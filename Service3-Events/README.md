# 🗓️ Service3 - Eventos y Notificaciones

Este microservicio forma parte del sistema distribuido de un club de atletismo. Su función principal es **gestionar eventos** y **enviar notificaciones en tiempo real** a los usuarios cuando se crean nuevos eventos.

---

## 🚀 Funcionalidad principal

- 📅 **Gestión de eventos**:
    - Crear un nuevo evento.
    - Listar eventos disponibles.
    - Eliminar eventos por ID.

- 🔔 **Sistema de notificaciones**:
    - Cuando se crea un evento, se publica una notificación en RabbitMQ.
    - El frontend escucha vía WebSocket para mostrar estas notificaciones.
    - Se puede consultar el historial de eventos no vistos por usuario desde la última conexión.

- 🧠 **Exposición de datos vía gRPC**:
    - Servicio gRPC que permite al Service1 gestionar eventos y notificaciones de usuarios.

---

## 🗂️ Estructura del proyecto

```bash
src/main/java/com/example/service3/
├── config/                 # Configuración de RabbitMQ y WebSocket
├── dto/                   # DTOs para notificaciones por WebSocket
├── entities/              # Entidades JPA: Event
├── grpc/                  # Servicio gRPC EventoServiceGrpcImpl
├── messaging/             # Publicación en cola de notificación
├── repositories/          # Interfaces JPA
├── services/              # Lógica de negocio desacoplada
└── Service3Application.java