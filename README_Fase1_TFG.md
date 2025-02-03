
# Fase 1: Versión con funcionalidad básica

## **Tareas de desarrollo**

### **Backend con API REST**
- [x] **Seleccionar tecnologías (Spring Boot y MySQL)**  
- [x] **Implementar API REST**  
  - [x] Configurar URLs con prefijo `/api/v1`  
  - [x] Usar métodos HTTP adecuados (GET, PUT, POST, DELETE)  
  - [x] Configurar códigos de estado de respuesta correctamente  
  - [x] Devolver header `Location` en operaciones de creación  
  - [x] Implementar filtrado y paginación en listados  
- [x] **Gestión de usuarios y seguridad**  
  - [x] Configurar Spring Security para gestión de usuarios  
  - [x] Mostrar error de acceso si un usuario no tiene permisos  
- [x] **Configurar HTTPS en el puerto 443**  
- [x] **Almacenar imágenes en la base de datos**  
- [x] **Desactivar protección contra CSRF**  
- [x] **Organización del repositorio:** Guardar código fuente en la carpeta `backend`  
- [x] **Cargar datos de ejemplo en la base de datos**  
- [x] **Documentar la API REST con OpenAPI**  
  - [x] Crear el fichero `api-docs.yaml`  
  - [x] Generar fichero HTML de la documentación en `backend/api-docs`  
- [x] **Incluir fichero de pruebas de la API REST (Postman o similar)**

### **Frontend con tecnología SPA**
- [x] **Seleccionar tecnologías (Angular)**  
- [x] **Comunicación con backend usando API REST**  
- [x] **Diseñar la interfaz de usuario con librerías de componentes**  
  - [x] Usar librerías como `ng-bootstrap` o `angular-material`  
- [x] **Seguir buenas prácticas de diseño Angular**  
  - [x] Separar lógica de gestión del interfaz en componentes  
  - [x] Implementar servicios para la conexión con el backend  
- [x] **Implementar páginas de error personalizadas**  
- [x] **Implementar paginación en listados (10 elementos por página)**  
- [x] **Organización del repositorio:** Guardar código fuente en la carpeta `frontend`

## **Calidad del código**
- [ ] **Formatear el código y usar reglas de estilo uniformes**  
- [x] **Escribir código y comentarios en inglés**  
- [x] **Utilizar nombres descriptivos para variables, clases e interfaces**  
- [x] **Evitar código duplicado**  
- [x] **Mantener la complejidad de los métodos baja**  
- [x] **Utilizar librería de logs en lugar de `System.out.println()`**  
- [x] **Desacoplar los módulos del backend (uso correcto de `@Service` y `@Repository`)**

## **Pruebas automáticas**
- [ ] **Implementar pruebas E2E de interfaz de usuario (preferentemente con Selenium)**  
- [x] **Implementar pruebas de API REST (preferentemente con Rest Assured)**  

## **Empaquetado con Docker y Docker Compose**
- [x] **Crear imagen Docker del backend con soporte HTTPS**  
- [x] **Publicar frontend Angular como recurso estático del backend**  
- [x] **Subir la imagen a DockerHub**  
- [x] **Crear fichero `docker-compose.yml`**  
  - [x] Usar imagen MySQL estándar de DockerHub  
  - [x] Configurar variables de entorno para la base de datos y la aplicación  
  - [x] Configurar healthcheck para el contenedor de la base de datos  

## **Documentación**
- [x] **Capturas de pantalla de las páginas principales**  
  - [x] Añadir descripción breve de cada página  
- [x] **Diagrama de navegación**  
- [x] **Diagrama de entidades de la base de datos**  
- [x] **Diagrama de clases del backend**  
- [x] **Diagrama de clases y templates del frontend SPA**  
- [x] **Documentación para la construcción de la imagen Docker**  
  - [x] Comando de construcción de la imagen Docker  
  - [ ] Documentación del repositorio DockerHub  
- [x] **Instrucciones para ejecutar la aplicación con `docker-compose`**  
- [ ] **Vídeo de presentación (entre 2 y 3 minutos)**  
  - [ ] Subir el vídeo a YouTube  
  - [ ] Enlazar el vídeo en el inicio del README  
