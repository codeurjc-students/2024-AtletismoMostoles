
# Fase 1: Versión con funcionalidad básica

## **Tareas de desarrollo**

### **Backend con API REST**
- [x] **Seleccionar tecnologías (Spring Boot y MySQL)**  
- [x] **Implementar API REST**  
  - [x] Configurar URLs con prefijo `/api/v1`  
  - [x] Usar métodos HTTP adecuados (GET, PUT, POST, DELETE)  
  - [x] Configurar códigos de estado de respuesta correctamente  
  - [ ] Devolver header `Location` en operaciones de creación  
  - [x] Implementar filtrado y paginación en listados  
- [ ] **Gestión de usuarios y seguridad**  
  - [ ] Configurar Spring Security para gestión de usuarios  
  - [ ] Mostrar error de acceso si un usuario no tiene permisos  
- [x] **Configurar HTTPS en el puerto 443**  
- [x] **Almacenar imágenes en la base de datos**  
- [ ] **Desactivar protección contra CSRF**  
- [x] **Organización del repositorio:** Guardar código fuente en la carpeta `backend`  
- [ ] **Cargar datos de ejemplo en la base de datos**  
- [ ] **Documentar la API REST con OpenAPI**  
  - [ ] Crear el fichero `api-docs.yaml`  
  - [ ] Generar fichero HTML de la documentación en `backend/api-docs`  
- [x] **Incluir fichero de pruebas de la API REST (Postman o similar)**

### **Frontend con tecnología SPA**
- [x] **Seleccionar tecnologías (Angular)**  
- [x] **Comunicación con backend usando API REST**  
- [x] **Diseñar la interfaz de usuario con librerías de componentes**  
  - [x] Usar librerías como `ng-bootstrap` o `angular-material`  
- [x] **Seguir buenas prácticas de diseño Angular**  
  - [x] Separar lógica de gestión del interfaz en componentes  
  - [x] Implementar servicios para la conexión con el backend  
- [ ] **Implementar páginas de error personalizadas**  
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
- [ ] **Crear imagen Docker del backend con soporte HTTPS**  
- [ ] **Publicar frontend Angular como recurso estático del backend**  
- [ ] **Subir la imagen a DockerHub**  
- [ ] **Crear fichero `docker-compose.yml`**  
  - [ ] Usar imagen MySQL estándar de DockerHub  
  - [ ] Configurar variables de entorno para la base de datos y la aplicación  
  - [ ] Configurar healthcheck para el contenedor de la base de datos  

## **Documentación**
- [ ] **Capturas de pantalla de las páginas principales**  
  - [ ] Añadir descripción breve de cada página  
- [ ] **Diagrama de navegación**  
- [ ] **Diagrama de entidades de la base de datos**  
- [ ] **Diagrama de clases del backend**  
- [ ] **Diagrama de clases y templates del frontend SPA**  
- [ ] **Documentación para la construcción de la imagen Docker**  
  - [ ] Comando de construcción de la imagen Docker  
  - [ ] Documentación del repositorio DockerHub  
- [ ] **Instrucciones para ejecutar la aplicación con `docker-compose`**  
- [ ] **Vídeo de presentación (entre 2 y 3 minutos)**  
  - [ ] Subir el vídeo a YouTube  
  - [ ] Enlazar el vídeo en el inicio del README  
