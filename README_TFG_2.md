# 🏃‍♂️ TFG - Sistema Distribuido para Club de Atletismo

Este proyecto es la continuación del TFG original basado en una aplicación monolítica. Se ha rediseñado usando una arquitectura de microservicios para mejorar la escalabilidad, el mantenimiento y la especialización de cada funcionalidad.

---

## 🧩 Microservicios del sistema

### 🔹 Service1 - Frontend Gateway
- Rol: punto de entrada para el frontend Angular.
- Exposición REST.
- Se comunica por gRPC con el resto de servicios.

### 🔹 Service2 - Resultados y PDFs
- Gestión de resultados deportivos por atleta y evento.
- Generación de informes PDF simulados.
- Comunicación vía gRPC y RabbitMQ.

### 🔹 Service3 - Eventos y Notificaciones
- Gestión de eventos.
- Emisión de notificaciones en tiempo real al frontend vía WebSocket.
- Comunicación vía gRPC y RabbitMQ.

---

## 🛰️ Comunicación entre servicios

| Origen     | Destino     | Protocolo      | Propósito                         |
|------------|-------------|----------------|-----------------------------------|
| `Service1` | `Service2`  | gRPC           | Consultar y guardar resultados    |
| `Service1` | `Service3`  | gRPC           | Crear, listar o borrar eventos    |
| `Service1` | RabbitMQ    | WebSocket      | Mostrar notificaciones al usuario |
| `Service2` | RabbitMQ    | AMQP           | Gestionar generación de PDFs      |
| `Service3` | RabbitMQ    | AMQP           | Publicar nuevas notificaciones    |

---

## 📁 Estructura del repositorio

```
/TFG-Proyecto-Atletismo/
│
├── frontend-angular/             # Cliente Angular
├── service1-gateway/             # Puerta de entrada (REST + gRPC)
├── Service2-Results-PDF/         # Resultados + generación de PDFs
├── Service3-Events/              # Gestión de eventos y notificaciones
├── shared-protos/                # Archivos .proto comunes para gRPC
├── docker-compose.yml            # (Opcional) Infraestructura RabbitMQ/MySQL
└── README.md                     # Este archivo
```

---

## 🧪 Tecnologías utilizadas

- Spring Boot 3.5.0
- Angular
- gRPC + Protobuf
- Spring Data JPA + MySQL
- Spring WebSocket
- Spring AMQP + RabbitMQ

---

## 🔧 Instrucciones básicas

### Compilar un servicio individual

```bash
cd Service2-Results-PDF
mvn clean install -DskipTests
```

### Ejecutar un servicio

```bash
mvn spring-boot:run
```

> Nota: asegúrate de tener MySQL y RabbitMQ activos para que todos los servicios funcionen correctamente.

---

## 👥 Autores

- Autor original: [Tu nombre aquí]
- Tutor académico: [Nombre del tutor]

---

## ✅ Estado del sistema

| Servicio     | Estado  |
|--------------|---------|
| Service1     | 🟡 En progreso |
| Service2     | ✅ Completo |
| Service3     | ✅ Completo |
| shared-protos| ✅ Definido |
| Frontend     | 🟡 En desarrollo |
