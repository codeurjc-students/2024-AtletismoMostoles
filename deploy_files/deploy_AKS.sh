#!/usr/bin/env bash
set -euo pipefail

############################################
#               CONFIG                     #
############################################
SUBSCRIPTION_ID="0bf7e759-f3b7-4c7f-a702-bb1121cdb095"
LOCATION="westeurope"
RESOURCE_GROUP="rg-tfg-weu"
AKS_NAME="aks-tfg-cluster"
AKS_NODE_VM_SIZE="Standard_B2s"

# ===== MySQL Flexible Server =====
MYSQL_SERVER_NAME="mysql-tfg-server"   # must be globally unique in Azure
MYSQL_ADMIN_USER="mysqladmin"
MYSQL_ADMIN_PASSWORD="29042013Sa"
MYSQL_SKU="Standard_B1ms"
MYSQL_STORAGE_GB=32
MYSQL_VERSION="8.4"
DB_SERVICE1="service1_db"
DB_SERVICE2="service2_db"
DB_SERVICE3="service3_db"

# ===== Storage Account (PDFs/Images) =====
STG_ACCOUNT="tfgpdfs"                   # must be globally unique in Azure
STG_CONTAINER_PDFS="resultspdf"

# ===== Docker images =====
# Option A: Docker Hub
USE_DOCKER_HUB=true
DOCKER_HUB_USER="saac04"
IMG_FRONTEND="${DOCKER_HUB_USER}/frontend:latest"
IMG_SERVICE1="${DOCKER_HUB_USER}/service1-backend:latest"
IMG_SERVICE2="${DOCKER_HUB_USER}/service2-result:latest"
IMG_SERVICE3="${DOCKER_HUB_USER}/service3-events:latest"

# Option B: Azure Container Registry (ACR)
USE_ACR=false
ACR_NAME="tfgacr123456"                 # must be globally unique in Azure
# When USE_ACR=true, image references must be: ${ACR_NAME}.azurecr.io/<name>:<tag>

# ===== Kubernetes =====
K8S_NAMESPACE_DEFAULT="default"

# Local manifest paths
K8S_DIR="k8s"

FRONTEND_DEP="${K8S_DIR}/frontend/deployment.yaml"
FRONTEND_SVC="${K8S_DIR}/frontend/service.yaml"
FRONTEND_ING="${K8S_DIR}/frontend/ingress.yaml"
FRONTEND_HPA="${K8S_DIR}/frontend/hpa.yaml"
FRONTEND_PDB="${K8S_DIR}/frontend/pdb.yaml"

S1_CM="${K8S_DIR}/service1/configmap.yaml"
S1_SEC="${K8S_DIR}/service1/secret.yaml"
S1_DEP="${K8S_DIR}/service1/deployment.yaml"
S1_SVC="${K8S_DIR}/service1/service.yaml"
S1_HPA="${K8S_DIR}/service1/hpa.yaml"
S1_PDB="${K8S_DIR}/service1/pdb.yaml}"

S2_CM="${K8S_DIR}/service2/configmap.yaml"
S2_SEC="${K8S_DIR}/service2/secret.yaml"
S2_DEP="${K8S_DIR}/service2/deployment.yaml"
S2_SVC="${K8S_DIR}/service2/service.yaml"
S2_HPA="${K8S_DIR}/service2/hpa.yaml"
S2_PDB="${K8S_DIR}/service2/pdb.yaml"

S3_CM="${K8S_DIR}/service3/configmap.yaml"
S3_SEC="${K8S_DIR}/service3/secret.yaml"
S3_DEP="${K8S_DIR}/service3/deployment.yaml"
S3_SVC="${K8S_DIR}/service3/service.yaml"
S3_HPA="${K8S_DIR}/service3/hpa.yaml"
S3_PDB="${K8S_DIR}/service3/pdb.yaml"

# RabbitMQ Operator + Cluster CR
RABBITMQ_OPERATOR_URL="https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"
RABBITMQ_CLUSTER_YAML="${K8S_DIR}/rabbitmq/rabbitmq-cluster.yaml"

# ===== Ingress-NGINX Controller and cert-manager =====
INSTALL_INGRESS_NGINX=true
INSTALL_CERT_MANAGER=true
LETSENCRYPT_EMAIL="s.aguiar1.2020@alumnos.urjc.es"
CLUSTER_ISSUER_NAME="letsencrypt-prod"
CERT_MANAGER_MANIFEST="https://github.com/cert-manager/cert-manager/releases/download/v1.14.2/cert-manager.yaml"

############################################
#         Helper functions (logs)          #
############################################
log()  { echo -e "\n\033[1;36m[INFO]\033[0m $*"; }
warn() { echo -e "\n\033[1;33m[WARN]\033[0m $*"; }
err()  { echo -e "\n\033[1;31m[ERROR]\033[0m $*" >&2; }

# Waits until a Kubernetes Service has at least one endpoint
wait_for_endpoints() {
  local ns="$1" svc="$2" tries="${3:-40}"
  for i in $(seq 1 "$tries"); do
    if kubectl -n "$ns" get endpoints "$svc" -o jsonpath='{.subsets[0].addresses[0].ip}' 2>/dev/null | grep -qE '^[0-9]'; then
      return 0
    fi
    sleep 3
  done
  return 1
}

############################################
#        Azure subscription (optional)     #
############################################
if [[ -n "${SUBSCRIPTION_ID}" ]]; then
  log "Selecting subscription ${SUBSCRIPTION_ID}"
  az account set --subscription "${SUBSCRIPTION_ID}"
fi

############################################
#        Resource Group                    #
############################################
log "Ensuring Resource Group: ${RESOURCE_GROUP} (${LOCATION})"
az group create -n "${RESOURCE_GROUP}" -l "${LOCATION}" 1>/dev/null

############################################
#        Azure Container Registry (opt)    #
############################################
if [[ "${USE_ACR}" == true ]]; then
  log "Ensuring ACR: ${ACR_NAME}"
  if ! az acr show -n "${ACR_NAME}" -g "${RESOURCE_GROUP}" 1>/dev/null 2>&1; then
    az acr create -n "${ACR_NAME}" -g "${RESOURCE_GROUP}" --sku Basic --location "${LOCATION}" 1>/dev/null
  fi
  log "Login to ACR"
  az acr login -n "${ACR_NAME}"
fi

############################################
#        AKS (with Cluster Autoscaler)     #
############################################
log "Ensuring AKS: ${AKS_NAME}"
if ! az aks show -g "${RESOURCE_GROUP}" -n "${AKS_NAME}" 1>/dev/null 2>&1; then
  az aks create \
    --resource-group "${RESOURCE_GROUP}" \
    --name "${AKS_NAME}" \
    --node-count 1 \
    --node-vm-size "${AKS_NODE_VM_SIZE}" \
    --enable-cluster-autoscaler --min-count 1 --max-count 3 \
    --generate-ssh-keys \
    --location "${LOCATION}"
else
  # Make sure autoscaler is enabled on the default nodepool
  log "Enabling Cluster Autoscaler on nodepool1"
  az aks nodepool update \
    --resource-group "${RESOURCE_GROUP}" \
    --cluster-name "${AKS_NAME}" \
    --name nodepool1 \
    --enable-cluster-autoscaler --min-count 1 --max-count 3
fi

log "Fetching AKS credentials"
az aks get-credentials -g "${RESOURCE_GROUP}" -n "${AKS_NAME}" --overwrite-existing

############################################
#   STATIC PIP for Ingress in NODE RG      #
############################################
# We pre-create a static Public IP (Standard) in the AKS node RG and force
# the ingress-nginx Service to use it. This makes deployments deterministic.
NODE_RG=$(az aks show -g "${RESOURCE_GROUP}" -n "${AKS_NAME}" --query nodeResourceGroup -o tsv)

# Create or ensure a static PIP for the ingress controller
if ! az network public-ip show -g "${NODE_RG}" -n pip-ingress-tfg 1>/dev/null 2>&1; then
  az network public-ip create -g "${NODE_RG}" -n pip-ingress-tfg --sku Standard --allocation-method static --version IPv4 1>/dev/null
fi
PUBIP=$(az network public-ip show -g "${NODE_RG}" -n pip-ingress-tfg --query ipAddress -o tsv)
export PUBIP

############################################
#        Storage Account + Container       #
############################################
log "Ensuring Storage Account: ${STG_ACCOUNT}"
if ! az storage account show -n "${STG_ACCOUNT}" -g "${RESOURCE_GROUP}" 1>/dev/null 2>&1; then
  az storage account create \
    --name "${STG_ACCOUNT}" \
    --resource-group "${RESOURCE_GROUP}" \
    --location "${LOCATION}" \
    --sku Standard_LRS 1>/dev/null
fi

log "Ensuring Blob Container: ${STG_CONTAINER_PDFS}"
CONN_STR=$(az storage account show-connection-string -n "${STG_ACCOUNT}" -g "${RESOURCE_GROUP}" --query connectionString -o tsv)
az storage container create \
   --name "${STG_CONTAINER_PDFS}" \
   --connection-string "${CONN_STR}" 1>/dev/null

# Keep K8s secret in sync with the latest Storage connection string
log "Creating/Updating K8s secret 'azure-storage-conn' with current connection string"
kubectl create secret generic azure-storage-conn \
  --from-literal=AZURE_STORAGE_CONNECTION_STRING="${CONN_STR}" \
  --dry-run=client -o yaml | kubectl apply -f -

############################################
#        MySQL Flexible Server + DBs       #
############################################
log "Ensuring MySQL Flexible Server: ${MYSQL_SERVER_NAME}"
if ! az mysql flexible-server show \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${MYSQL_SERVER_NAME}" 1>/dev/null 2>&1; then

  # Create server non-interactively with public network disabled
  az mysql flexible-server create \
    --resource-group "${RESOURCE_GROUP}" \
    --name "${MYSQL_SERVER_NAME}" \
    --location "${LOCATION}" \
    --admin-user "${MYSQL_ADMIN_USER}" \
    --admin-password "${MYSQL_ADMIN_PASSWORD}" \
    --sku-name "${MYSQL_SKU}" \
    --storage-size "${MYSQL_STORAGE_GB}" \
    --version "${MYSQL_VERSION}" \
    --public-access none \
    --yes
fi

log "Ensuring DBs: ${DB_SERVICE1}, ${DB_SERVICE2}, ${DB_SERVICE3}"
for db in "${DB_SERVICE1}" "${DB_SERVICE2}" "${DB_SERVICE3}"; do
  az mysql flexible-server db create \
    --resource-group "${RESOURCE_GROUP}" \
    --server "${MYSQL_SERVER_NAME}" \
    --database-name "${db}" 1>/dev/null || true
done

# LAB ONLY: Enable public network and open firewall to all
warn "Enabling public network access for MySQL (lab only)"
az mysql flexible-server update \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${MYSQL_SERVER_NAME}" \
  --public-access Enabled \
  --only-show-errors 1>/dev/null

# Wait until server is 'Ready'
for i in {1..40}; do
  STATE=$(az mysql flexible-server show \
    --resource-group "${RESOURCE_GROUP}" \
    --name "${MYSQL_SERVER_NAME}" \
    --query "state" -o tsv 2>/dev/null || echo "Unknown")
  [[ "$STATE" == "Ready" ]] && break
  sleep 3
done
log "MySQL server state: ${STATE}"

# Upsert firewall 'allowAll' via ARM REST API (handles CLI quirks)
SUB_ID="${SUBSCRIPTION_ID:-$(az account show --query id -o tsv)}"
FW_BASE="https://management.azure.com/subscriptions/${SUB_ID}/resourceGroups/${RESOURCE_GROUP}/providers/Microsoft.DBforMySQL/flexibleServers/${MYSQL_SERVER_NAME}/firewallRules/allowAll"
FW_BOD='{"properties":{"startIpAddress":"0.0.0.0","endIpAddress":"255.255.255.255"}}'
API_VERSIONS=("2025-06-30" "2025-03-30" "2024-12-30" "2024-06-30" "2023-12-30")
FW_OK=false
for V in "${API_VERSIONS[@]}"; do
  FW_URL="${FW_BASE}?api-version=${V}"
  log "Ensuring firewall rule 'allowAll' via REST (api-version=${V})"
  if az rest --method put --url "${FW_URL}" --body "${FW_BOD}" --only-show-errors 1>/dev/null; then
    FW_OK=true
    break
  else
    warn "Firewall PUT failed with api-version ${V}, trying next…"
  fi
done
if [[ "${FW_OK}" == true ]]; then
  RULE_START=$(az rest --method get --url "${FW_URL}" \
    --only-show-errors --query 'properties.startIpAddress' -o tsv 2>/dev/null || echo "")
  [[ "${RULE_START}" == "0.0.0.0" ]] && log "Firewall rule 'allowAll' configured." || warn "Rule not confirmed; continuing."
else
  warn "Could not create firewall rule via REST with any supported api-version."
fi

MYSQL_FQDN=$(az mysql flexible-server show \
  --resource-group "${RESOURCE_GROUP}" \
  --name "${MYSQL_SERVER_NAME}" \
  --query "fullyQualifiedDomainName" -o tsv)

############################################
#        Ingress-NGINX (Controller)        #
############################################
if [[ "${INSTALL_INGRESS_NGINX}" == true ]]; then
  log "Installing/Upgrading ingress-nginx via Helm (with static IP + Local ETP)"
  helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx 1>/dev/null || true
  helm repo update 1>/dev/null
  kubectl get ns ingress-nginx 1>/dev/null 2>&1 || kubectl create ns ingress-nginx

  # NOTE:
  # - externalTrafficPolicy=Local creates a dedicated healthCheckNodePort used by Azure LB probes
  # - We pass the static PIP and Azure-specific annotations directly through Helm values
  HELM_SET=(
    --set controller.service.externalTrafficPolicy=Local
    --set controller.service.loadBalancerIP="${PUBIP}"
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-pip-name"="pip-ingress-tfg"
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-resource-group"="${NODE_RG}"
    --set controller.service.annotations."service\.beta\.kubernetes\.io/azure-load-balancer-health-probe-protocol"="tcp"
  )

  if ! helm status ingress-nginx -n ingress-nginx 1>/dev/null 2>&1; then
    helm install ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx "${HELM_SET[@]}"
  else
    helm upgrade ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx "${HELM_SET[@]}"
  fi

  # Wait for controller
  kubectl rollout status deploy/ingress-nginx-controller -n ingress-nginx --timeout=180s || true

  # Wait until Service shows the exact static IP
  log "Waiting for ingress-nginx external IP (fixed ${PUBIP})..."
  EXT_IP=""
  for i in {1..40}; do
    EXT_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)
    [[ "${EXT_IP}" == "${PUBIP}" ]] && break || sleep 5
  done
  log "Ingress-NGINX LB IP: ${EXT_IP:-<pending>}"

  # --- NSG rules so Azure LB can reach NodePorts (data path) and HCNP (probe) ---
  # Resolve NSG from subnet (preferred) or from a node NIC (fallback)
  NSG_ID=$(az network vnet list -g "${NODE_RG}" --query "[0].subnets[0].networkSecurityGroup.id" -o tsv 2>/dev/null || echo "")
  if [[ -z "${NSG_ID}" || "${NSG_ID}" == "null" ]]; then
    NIC=$(az network nic list -g "${NODE_RG}" --query "[?contains(name,'aks-nodepool')].name | [0]" -o tsv)
    NSG_ID=$(az network nic show -g "${NODE_RG}" -n "${NIC}" --query "networkSecurityGroup.id" -o tsv)
  fi

  if [[ -n "${NSG_ID}" && "${NSG_ID}" != "null" ]]; then
    NSG_NAME=$(basename "${NSG_ID}")
    NSG_RG=$(echo "${NSG_ID}" | awk -F/ '{for(i=1;i<=NF;i++) if($i=="resourceGroups"){print $(i+1);break}}')

    # Open full NodePort range for reliability across upgrades (includes HCNP)
    az network nsg rule create -g "${NSG_RG}" --nsg-name "${NSG_NAME}" \
      -n aks_allow_lb_to_nodeports --priority 300 --access Allow --protocol Tcp \
      --direction Inbound --source-address-prefixes AzureLoadBalancer \
      --destination-port-ranges 30000-32767 --only-show-errors 1>/dev/null || true

    # Optionally: open the exact healthCheckNodePort explicitly (harmless if duplicated by the range)
    HCNP=$(kubectl -n ingress-nginx get svc ingress-nginx-controller -o jsonpath='{.spec.healthCheckNodePort}' 2>/dev/null || echo "")
    if [[ -n "${HCNP}" ]]; then
      az network nsg rule create -g "${NSG_RG}" --nsg-name "${NSG_NAME}" \
        -n aks_allow_lb_probe --priority 301 --access Allow --protocol Tcp \
        --direction Inbound --source-address-prefixes AzureLoadBalancer \
        --destination-port-ranges "${HCNP}" --only-show-errors 1>/dev/null || true
    fi
  else
    warn "Could not determine NSG; if you see external timeouts, create NodePort/HCNP rules manually."
  fi
fi

############################################
#        cert-manager (optional)           #
############################################
if [[ "${INSTALL_CERT_MANAGER}" == true ]]; then
  log "Installing cert-manager"
  kubectl get ns cert-manager 1>/dev/null 2>&1 || kubectl create ns cert-manager
  kubectl apply -f "${CERT_MANAGER_MANIFEST}"

  # Wait for deployments before applying ClusterIssuer
  kubectl wait --for=condition=Available deploy/cert-manager -n cert-manager --timeout=180s
  kubectl wait --for=condition=Available deploy/cert-manager-webhook -n cert-manager --timeout=180s
  kubectl wait --for=condition=Available deploy/cert-manager-cainjector -n cert-manager --timeout=180s

  log "Creating ClusterIssuer (${CLUSTER_ISSUER_NAME})"
  cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: ${CLUSTER_ISSUER_NAME}
spec:
  acme:
    email: ${LETSENCRYPT_EMAIL}
    server: https://acme-v02.api.letsencrypt.org/directory
    privateKeySecretRef:
      name: letsencrypt-private-key
    solvers:
    - http01:
        ingress:
          class: nginx
EOF
fi

############################################
#   RabbitMQ Operator + Cluster resource   #
############################################
log "Installing RabbitMQ Cluster Operator"
kubectl apply -f "${RABBITMQ_OPERATOR_URL}"
kubectl wait --for=condition=Established crd/rabbitmqclusters.rabbitmq.com --timeout=180s

if [[ -f "${RABBITMQ_CLUSTER_YAML}" ]]; then
  log "Applying RabbitmqCluster: ${RABBITMQ_CLUSTER_YAML}"
  kubectl apply -f "${RABBITMQ_CLUSTER_YAML}"
else
  warn "RabbitmqCluster CR file not found at ${RABBITMQ_CLUSTER_YAML}"
fi

############################################
#        Build & Push Docker images        #
############################################
if [[ "${USE_DOCKER_HUB}" == true && "${USE_ACR}" == false ]]; then
  warn "Using Docker Hub (${DOCKER_HUB_USER}). Ensure you have 'docker login'."

  log "Build & Push frontend"
  docker build -t "${IMG_FRONTEND}" -f docker/frontend/Dockerfile .
  docker push "${IMG_FRONTEND}"

  log "Build & Push service1"
  docker build -t "${IMG_SERVICE1}" -f docker/service1/Dockerfile .
  docker push "${IMG_SERVICE1}"

  log "Build & Push service2"
  docker build -t "${IMG_SERVICE2}" -f docker/service2/Dockerfile .
  docker push "${IMG_SERVICE2}"

  log "Build & Push service3"
  docker build -t "${IMG_SERVICE3}" -f docker/service3/Dockerfile .
  docker push "${IMG_SERVICE3}"
fi

if [[ "${USE_ACR}" == true ]]; then
  warn "Using ACR (${ACR_NAME}). Tag your images as: ${ACR_NAME}.azurecr.io/<image>:<tag>"
  # Example (uncomment if you want to build/push directly to ACR):
  # docker build -t ${ACR_NAME}.azurecr.io/frontend:latest -f docker/frontend/Dockerfile .
  # docker push ${ACR_NAME}.azurecr.io/frontend:latest
fi

############################################
#        Apply Kubernetes manifests        #
############################################
# FRONTEND (deploy + service first)
log "Applying manifests: FRONTEND"
kubectl apply -f "${FRONTEND_DEP}"
kubectl apply -f "${FRONTEND_SVC}"
[[ -f "${FRONTEND_HPA}" ]] && kubectl apply -f "${FRONTEND_HPA}"
[[ -f "${FRONTEND_PDB}" ]] && kubectl apply -f "${FRONTEND_PDB}"

# Build a deterministic hostname with the fixed Public IP
FRONTEND_HOST="frontend.${PUBIP}.nip.io"
export FRONTEND_HOST
log "Using frontend host: ${FRONTEND_HOST}"

# Wait until the Service has endpoints before creating Ingress (prevents 503/ACME flakiness)
if ! wait_for_endpoints "${K8S_NAMESPACE_DEFAULT}" "frontend" 40; then
  warn "Service 'frontend' has no endpoints yet; continuing, but first ACME attempt may be delayed."
fi

# Apply the Ingress with the dynamic host via envsubst (preferred) or sed fallback
if command -v envsubst >/dev/null 2>&1; then
  envsubst < "${FRONTEND_ING}" | kubectl apply -f -
else
  TMP_ING=$(mktemp)
  sed -E "s/frontend\.[0-9.]+\.nip\.io/${FRONTEND_HOST}/g" "${FRONTEND_ING}" > "${TMP_ING}"
  kubectl apply -f "${TMP_ING}"
  rm -f "${TMP_ING}"
fi

# SERVICE1
log "Applying manifests: SERVICE1"
kubectl apply -f "${S1_CM}"
kubectl apply -f "${S1_SEC}"
kubectl apply -f "${S1_DEP}"
kubectl apply -f "${S1_SVC}"
[[ -f "${S1_HPA}" ]] && kubectl apply -f "${S1_HPA}"
[[ -f "${S1_PDB}" ]] && kubectl apply -f "${S1_PDB}"

# SERVICE2
log "Applying manifests: SERVICE2"
kubectl apply -f "${S2_CM}"
kubectl apply -f "${S2_SEC}"
kubectl apply -f "${S2_DEP}"
kubectl apply -f "${S2_SVC}"
[[ -f "${S2_HPA}" ]] && kubectl apply -f "${S2_HPA}"
[[ -f "${S2_PDB}" ]] && kubectl apply -f "${S2_PDB}"

# Force pods to reload the updated Storage connection string
log "Restarting service2 to pick up updated Storage connection string"
kubectl rollout restart deploy/service2-backend

# SERVICE3
log "Applying manifests: SERVICE3"
kubectl apply -f "${S3_CM}"
kubectl apply -f "${S3_SEC}"
kubectl apply -f "${S3_DEP}"
kubectl apply -f "${S3_SVC}"
[[ -f "${S3_HPA}" ]] && kubectl apply -f "${S3_HPA}"
[[ -f "${S3_PDB}" ]] && kubectl apply -f "${S3_PDB}"

############################################
#   Optional: Retry certificate issuance   #
############################################
# If the previous issuance was 'invalid' (e.g., first-run timing),
# clean ACME challenge/order/secret to let cert-manager retry cleanly.
if [[ "${INSTALL_CERT_MANAGER}" == true ]]; then
  sleep 5
  READY=$(kubectl get certificate frontend-tls -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}' 2>/dev/null || echo "")
  if [[ "${READY}" != "True" ]]; then
    log "Re-triggering certificate issuance for ${FRONTEND_HOST}"
    kubectl -n default delete challenge -l "acme.cert-manager.io/dns-name=${FRONTEND_HOST}" --ignore-not-found
    kubectl -n default delete order     -l "acme.cert-manager.io/dns-name=${FRONTEND_HOST}" --ignore-not-found
    kubectl -n default delete secret frontend-tls --ignore-not-found
    kubectl -n default delete certificate frontend-tls --ignore-not-found

    if command -v envsubst >/dev/null 2>&1; then
      envsubst < "${FRONTEND_ING}" | kubectl apply -f -
    else
      TMP_ING=$(mktemp)
      sed -E "s/frontend\.[0-9.]+\.nip\.io/${FRONTEND_HOST}/g" "${FRONTEND_ING}" > "${TMP_ING}"
      kubectl apply -f "${TMP_ING}"
      rm -f "${TMP_ING}"
    fi
  fi
fi

############################################
#        Wait for rollouts (feedback)      #
############################################
log "Waiting for deployments rollout"
kubectl rollout status deploy/frontend --timeout=180s || true
kubectl rollout status deploy/service1-backend --timeout=180s || true
kubectl rollout status deploy/service2-backend --timeout=180s || true
kubectl rollout status deploy/service3-backend --timeout=180s || true

############################################
#                Summary                   #
############################################
log "Deployment completed."
echo "-------------------------------------------"
echo "Resource Group:     ${RESOURCE_GROUP}"
echo "AKS:                ${AKS_NAME}"
echo "MySQL FQDN:         ${MYSQL_FQDN}"
echo "Storage Account:    ${STG_ACCOUNT} (container: ${STG_CONTAINER_PDFS})"
echo "Ingress LB IP:      ${PUBIP}"
echo "Frontend Host:      https://frontend.${PUBIP}.nip.io"
echo "-------------------------------------------"
echo "Quick checks:"
echo "  curl -I http://${PUBIP}/"
echo "  curl -I -H 'Host: frontend.${PUBIP}.nip.io' http://${PUBIP}/"
echo "  curl -kI https://frontend.${PUBIP}.nip.io/"
echo "-------------------------------------------"