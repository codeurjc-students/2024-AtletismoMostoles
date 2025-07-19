# 🧩 Service1 - API Gateway y Gestión Principal

Este microservicio actúa como **puerta de entrada al sistema** y se encarga de gestionar toda la lógica que no ha sido delegada a microservicios independientes. También expone los datos al frontend Angular.

---

## 🚀 Funcionalidad principal

- 🧑‍🤝‍🧑 Gestión de usuarios, atletas, entrenadores, disciplinas y equipamiento.
- 🔐 Autenticación y autorización con Spring Security + JWT.
- 🌐 Comunicación con otros microservicios mediante **gRPC**:
    - `service2`: resultados y generación de PDFs.
    - `service3`: eventos y notificaciones.

- 📥 Comunicación asincrónica con RabbitMQ para:
    - Escuchar confirmaciones de PDF desde `service2` (cola B).
- 📡 Envío de notificaciones al frontend mediante **WebSocket**.

---

## 🗂️ Estructura del proyecto

```bash
src/main/java/com/example/service1/
├── config/                 # Configuraciones (CORS, OpenAPI, WebSocket)
├── dto/                   # DTOs para PDF, resultados y notificaciones
├── entities/              # Entidades JPA: Athlete, Coach, etc.
├── grpcclientes/          # Clientes gRPC hacia service2 y service3
├── listeners/             # Listener para cola B (confirmación de PDF)
├── repositories/          # Interfaces JPA
├── restcontrollers/       # Endpoints REST públicos y protegidos
├── security/              # Configuración de JWT y usuarios
├── services/              # Lógica de negocio para cada módulo
└── Service1Application.java
