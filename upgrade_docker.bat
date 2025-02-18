@echo off

REM Iniciar sesión en Docker sin exponer credenciales
echo Logging in to Docker...
docker login -u "%DOCKER_USERNAME%" -p "%DOCKER_ACCESS_TOKEN%"

REM Construir la imagen Docker
cd /d %~dp0
docker build -f docker/Dockerfile_backend -t %DOCKER_USERNAME%/backend:latest .

REM Subir la imagen a Docker Hub
docker push %DOCKER_USERNAME%/backend:latest

REM Reiniciar los contenedores con Docker Compose
cd docker
docker-compose down
docker-compose up -d
