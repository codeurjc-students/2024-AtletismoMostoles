# Documentación del Proyecto TFG WebApp

## 📌 Introducción
TFG WebApp es una aplicación desarrollada en Angular y Spring Boot para la gestión de eventos deportivos. La aplicación está contenida en Docker y utiliza MySQL como base de datos.

## 📷 Capturas de Pantalla
Se incluyen capturas de pantalla de las principales páginas de la aplicación, con una breve descripción de cada una.

- **Inicio**: Pantalla De Inicio con información basica del club y acceso a las demás pantallas
![index](diagrams/screenshots_pages/index.png)
- **Login**: Pantalla de autenticación de usuarios.
![login](diagrams/screenshots_pages/login.png)
- **Ranking**: Interfaz con un listado de atletas y los filtros sobre la lista.
![ranking](diagrams/screenshots_pages/ranking.png)
- **Clubmembers**: Interfaz con un listado de los entrenadores del club, y los filtros aplicables sobre este.
![clubmembers](diagrams/screenshots_pages/culbmembers.png)
- **Assosiation-atl**: Interfaz con el listado de disciplinas que se imparten en el club.
![assosiation-atl](diagrams/screenshots_pages/assosiation-atl.png)
- **Calendar-Events**: Interfaz con el calendario de enventos interactivo.
![calendar-events](diagrams/screenshots-pages/calendar-event.png)

## 🔗 Diagrama de Navegación
Se muestra la navegación entre las distintas páginas de la aplicación, con miniaturas de las capturas de pantalla.
## ![Diagrama de Navegacion](diagrams/Diagrama de Navegacion.png)

## 📊 Diagrama de Entidades de la Base de Datos
El siguiente diagrama muestra las entidades de la base de datos, sus atributos y relaciones. 

## ![Diagrama de Entidades de la BD](diagrams/DiagramaDeEntidades.png)

## 🖥️ Diagrama de Clases del Backend
Se presenta un diagrama que describe la estructura del backend, diferenciando **@RestController, @Service, @Repository** y **Entidades**.
## ![Diagrama de clases Backend](diagrams/Diagrama de Clases del Backend.jpeg)

## 🎨 Diagrama de Clases y Templates del Frontend
Se muestra la relación entre los componentes Angular, incluyendo **servicios, templates y relaciones padre-hijo**.
## ![Diagrama de clases Frontend](diagrams/Diagrama de Clases Frontend.jpeg)

## 🐳 Construcción de la Imagen Docker
Para construir la imagen Docker del backend, ejecutar el siguiente comando desde el directorio rais del proyecto:
```sh
docker build -t saac04/backend:latest -f docker/Dockerfile_backend .
```
Esto crea la imagen saac04/backend:latest en docker.

## 🚀 Ejecución de la Aplicación con Docker Compose
Para ejecutar la aplicación con Docker Compose, ejecutamos el siguiente código desde el directorio rais:
```sh
cd docker
docker-compose up -d
```
Esto inicia los contenedores del backend y la base de datos MySQL.
Una vez iniciado el contenedor la aplicación estara disponible en la url
```
https://localhost:443
```

## 🎥 Vídeo Demostrativo
Se ha subido un vídeo a YouTube mostrando las funcionalidades de la aplicación. Puedes verlo aquí: [Enlace al vídeo](https://youtube.com/tu-video).

