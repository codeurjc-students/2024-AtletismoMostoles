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
├── frontend/                     # Cliente Angular
├── service1-backend/             # Puerta de entrada (REST + gRPC)
├── Service2-Results-PDF/         # Resultados + generación de PDFs
├── Service3-Events/              # Gestión de eventos y notificaciones
├── shared-protos/                # Archivos .proto comunes para gRPC
├── docker/                       # Archivos Dockerfile para definicion de cada imagen
├── k8s/                          # Archivos de definición de los manifiestos kubernetes
├── deploy_files/                 # Archivos de despliegue de servicio en AKS sin IaC
├── infra/                        # Archivos necesario para el despliegue con IaC
...                               ...
├── README_TFG_2.md               # Este Archivo
...                               ...
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

## Guía de Despliegue — Atletismo Móstoles (AKS + MySQL + Storage)

Esta guía explica **dos formas** de desplegar *toda la infraestructura* y la aplicación en Azure:

- **A) Script “todo en uno”**: `deploy_AKS.sh` — ideal para demos/local.
- **B) Infraestructura como Código (IaC)** con **Bicep** — más declarativo y versionable.

---

## Requisitos

- **Azure CLI** (az) 2.55+
- **Bicep CLI** (v0.24+). Si usas `az`, ya trae soporte para Bicep en la mayoría de versiones.
- **kubectl** 1.28+
- **Helm 3**
- **Docker** (para build/push de imágenes)
- **Bash** (Linux/Mac/WSL2/Git Bash). En Windows, preferible **WSL2** o **PowerShell** + WSL.

Inicia sesión y elige suscripción:

```bash
az login
az account set --subscription "<TU_SUBSCRIPCION_UUID>"
```

---

## A) Despliegue con script `deploy_AKS.sh` (recomendado para demo)

El script prepara **todo**:
- Resource Group + AKS con autoscaling
- MySQL Flexible Server + BBDD
- Storage Account + contenedor
- **Ingress NGINX** con **IP pública estática** en el **Resource Group de nodos**
- Reglas NSG para NodePorts (necesarias en AKS + Standard LB)
- **cert-manager** + *ClusterIssuer* (Let’s Encrypt)
- **RabbitMQ operator** y tu `RabbitmqCluster`
- Build & push de imágenes Docker
- Aplicación (frontend + service1 + service2 + service3) + Ingress con **nip.io**

### 1) Ajusta variables (opcional)
Edita `deploy_files/deploy_AKS.sh` si quieres cambiar:
- Región, nombres, tamaños de VM
- Docker Hub vs ACR (`USE_DOCKER_HUB` / `USE_ACR`)
- Emails/hostnames
- Rutas de manifiestos K8s

### 2) Docker login (si usas Docker Hub)
```bash
docker login
```

### 3) Ejecuta
```bash
bash deploy_files/deploy_AKS.sh
```

Al final verás algo como:
```
Resource Group:     rg-tfg-weu
AKS:                aks-tfg-cluster
MySQL FQDN:         <xxxxx>.mysql.database.azure.com
Storage Account:    tfgpdfs (container: resultspdf)
Ingress LB IP:      98.71.203.209
Frontend Host:      frontend.98.71.203.209.nip.io
```

### 4) Verificación rápida
```bash
kubectl get pods -A
kubectl get svc -A
kubectl get ingress -A
```

Prueba HTTP/HTTPS (mientras se emite el certificado puedes usar `-k`):
```bash
curl -I -H "Host: frontend.<LB_IP>.nip.io" http://<LB_IP>/
curl -vkI https://frontend.<LB_IP>.nip.io/
```

> **nip.io** resuelve `<algo>.<IP>.nip.io` directamente a esa `IP`. Evita tener que comprar/dominar DNS para la demo.

---

## B) Despliegue con IaC (Bicep)

El Bicep declarativo de `infra/` crea:
- **AKS** con autoscaling
- **MySQL Flexible Server** (modo laboratorio: *publicNetworkAccess Enabled* + *firewall allowAll*)
- **Storage Account** + **contenedor**

> La **configuración de Ingress / IP estática / cert-manager / app** se realiza con comandos post-provisión (abajo). Esto te da control fino sin ensuciar el Bicep.

### 1) Estructura sugerida del repo

```
infra/
  main.bicep
  parameters.dev.json
deploy_files/
  deploy_AKS.sh
k8s/
  frontend/
    deployment.yaml
    service.yaml
    ingress.yaml
    hpa.yaml
    pdb.yaml
  service1/ ... (cm, secret, deployment, service, hpa, pdb)
  service2/ ... (cm, secret, deployment, service, hpa, pdb)
  service3/ ... (cm, secret, deployment, service, hpa, pdb)
  rabbitmq/
    rabbitmq-cluster.yaml
docker/
  frontend/Dockerfile
  service1/Dockerfile
  service2/Dockerfile
  service3/Dockerfile
```

## 2) Par\u00e1metros (ejemplo `infra/parameters.dev.json`)

> **No** pongas contraseñas reales; pásalas por CLI (`--parameters`) o usa Key Vault.

```jsonc
{
  "$schema": "https://schema.management.azure.com/schemas/2019-04-01/deploymentParameters.json#",
  "contentVersion": "1.0.0.0",
  "parameters": {
    "location": { "value": "westeurope" },
    "projectName": { "value": "tfg" },
    "aksName": { "value": "aks-tfg-cluster" },
    "aksVmSize": { "value": "Standard_DS2_v2" },
    "aksNodeCount": { "value": 1 },
    "aksMinCount": { "value": 1 },
    "aksMaxCount": { "value": 3 },
    "createLogAnalytics": { "value": true },
    "lawName": { "value": "log-tfg" },
    "mysqlServerName": { "value": "mysql-tfg-server" },
    "mysqlAdminUser": { "value": "mysqladmin" },
    "mysqlAdminPassword": { "value": "<PON_AQUI_UNA_PASSWORD_SEGURA>" },
    "mysqlVersion": { "value": "8.0.21" },
    "mysqlSkuName": { "value": "Standard_B1ms" },
    "mysqlStorageGB": { "value": 32 },
    "mysqlDatabases": {
      "value": ["service1_db","service2_db","service3_db"]
    },
    "storageAccountName": { "value": "tfgpdfs" },
    "pdfContainerName": { "value": "resultspdf" }
  }
}
```

### 3) Despliega con Bicep

Crea el **Resource Group** y lanza la implementación:

```bash
# 3.1 Grupo
az group create -n rg-tfg-weu -l westeurope

# 3.2 Implementación (IAAC)
az deployment group create \
  -g rg-tfg-weu \
  -f infra/main.bicep \
  -p infra/parameters.dev.json
```

Obtén **outputs** útiles (MySQL FQDN, Storage Connection String, etc.):
```bash
az deployment group show -g rg-tfg-weu -n <deploymentName> --query properties.outputs
```

### 4) Bootstrap K8s
Configurar el clúster y desplegar la app (Ingress NGINX + IP fija, NSG, cert-manager, RabbitMQ, manifiestos, etc.).

```bash
bash infra/bootstrap_k8s.sh
```

### 5) Verificar

```bash
kubectl get pods -A
kubectl get svc -A
kubectl get ingress -A

# HTTP debería devolver 308 (redirige a HTTPS)
curl -I -H "Host: frontend.${PUBIP}.nip.io" http://$PUBIP/

# HTTPS (usar -k si el cert aún se está emitiendo)
curl -vkI https://frontend.${PUBIP}.nip.io/
```

---

## Solución de problemas

- **`<pending>` en EXTERNAL-IP** (Service `ingress-nginx-controller`):  
  Asegura que la IP pública **existe en el Node RG** y que has anotado/forzado `loadBalancerIP` + `azure-pip-name` + `azure-load-balancer-resource-group`.
- **Timeouts desde fuera** (80/443):  
  Casi siempre es el **NSG** bloqueando **NodePorts**. Crea la regla `AzureLoadBalancer -> 30000–32767`.
- **Git Bash mete una ruta rara en la anotación `...health-probe-request-path`**:  
  Evítala usando **probe TCP** (`azure-load-balancer-health-probe-protocol=tcp`).
- **`Service "default/frontend" does not have any active Endpoint`** en logs de Ingress:  
  El Service/Deployment del frontend aún no expone endpoints (pods no listos). Revisa `kubectl get pods` y readiness/liveness.
- **Let’s Encrypt “invalid”**:  
  Borra `challenge`, `order` y el `Secret` del cert y re-aplica el Ingress (el script ya incluye un *retry* opcional).
- **Imágenes no tiran / ImagePullBackOff**:  
  Verifica `docker push`, credenciales de ACR (si aplican) y nombres/tags en los Deployments.
- **MySQL (laboratorio)**:  
  El Bicep deja **publicNetworkAccess Enabled** y un firewall **allowAll**. Úsalo solo para pruebas. Para producción: Private Access + VNET integration.

---

## Limpieza (evitar costes)

```bash
# Borra el Resource Group (elimina AKS, MySQL, Storage, etc.)
az group delete -n rg-tfg-weu --yes --no-wait
```

> Si el **MC_*** (Node RG) quedase “huérfano”, elimínalo igual desde Azure Portal o CLI.

---

## Notas finales

- Este repo mezcla un **script operativo** (`deploy_AKS.sh`) y **IaC con Bicep**: así tienes una ruta **rápida** y otra **declarativa**.
- El **script** automatiza también piezas que normalmente se gestionan fuera del Bicep (IP estática en Node RG, NSG de NodePorts, Helm releases, imágenes, etc.).
- Para **producción**, considera:
    - AKS con **Azure CNI**, **WAF + AGIC** o NGINX en modo “internal + App Gateway/WAF”,
    - MySQL con **acceso privado**, copias, HA, SKU GP/MO,
    - secretos en **Key Vault** + CSI Driver.

