#!/usr/bin/env bash
set -euo pipefail

############################################
#               CONFIG                     #
############################################
SUBSCRIPTION_ID="0bf7e759-f3b7-4c7f-a702-bb1121cdb095"
LOCATION="westeurope"
RESOURCE_GROUP="rg-tfg-weu"
AKS_NAME="aks-tfg-cluster"

# ===== TLS / cert-manager =====
INSTALL_CERT_MANAGER=true
LETSENCRYPT_EMAIL="s.aguiar1.2020@alumnos.urjc.es"
CLUSTER_ISSUER_NAME="letsencrypt-prod"
CERT_MANAGER_MANIFEST="https://github.com/cert-manager/cert-manager/releases/download/v1.14.2/cert-manager.yaml"

# ===== Ingress NGINX =====
INSTALL_INGRESS_NGINX=true
INGRESS_PIP_NAME="pip-ingress-tfg"   # Static PIP in the AKS node RG

# ===== Docker images =====
USE_DOCKER_HUB=true
DOCKER_HUB_USER="saac04"
IMG_FRONTEND="${DOCKER_HUB_USER}/frontend:latest"
IMG_SERVICE1="${DOCKER_HUB_USER}/service1-backend:latest"
IMG_SERVICE2="${DOCKER_HUB_USER}/service2-result:latest"
IMG_SERVICE3="${DOCKER_HUB_USER}/service3-events:latest"

# ===== K8s manifests =====
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
S1_PDB="${K8S_DIR}/service1/pdb.yaml"

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

# RabbitMQ
RABBITMQ_OPERATOR_URL="https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"
RABBITMQ_CLUSTER_YAML="${K8S_DIR}/rabbitmq/rabbitmq-cluster.yaml"

############################################
#         Helper functions (logs)          #
############################################
log()  { echo -e "\n\033[1;36m[INFO]\033[0m $*"; }
warn() { echo -e "\n\033[1;33m[WARN]\033[0m $*"; }
err()  { echo -e "\n\033[1;31m[ERROR]\033[0m $*" >&2; }

############################################
#            Pre-flight checks             #
############################################
for bin in az kubectl helm; do
  command -v "$bin" >/dev/null 2>&1 || { err "Missing $bin"; exit 1; }
done

############################################
#        Azure subscription & AKS          #
############################################
if [[ -n "${SUBSCRIPTION_ID}" ]]; then
  log "Selecting subscription ${SUBSCRIPTION_ID}"
  az account set --subscription "${SUBSCRIPTION_ID}"
fi

log "Fetching AKS credentials"
az aks get-credentials -g "${RESOURCE_GROUP}" -n "${AKS_NAME}" --overwrite-existing

# Node RG (managed RG where NIC/NSG/PIP live)
NODE_RG=$(az aks show -g "${RESOURCE_GROUP}" -n "${AKS_NAME}" --query nodeResourceGroup -o tsv)

############################################
#   Static Public IP for Ingress (node RG) #
############################################
# Purpose: Keep a fixed IP for ingress-nginx Service across redeploys
if ! az network public-ip show -g "${NODE_RG}" -n "${INGRESS_PIP_NAME}" 1>/dev/null 2>&1; then
  log "Creating static Public IP ${INGRESS_PIP_NAME} in ${NODE_RG}"
  az network public-ip create -g "${NODE_RG}" -n "${INGRESS_PIP_NAME}" --sku Standard --allocation-method static --version IPv4 1>/dev/null
fi
PUBIP=$(az network public-ip show -g "${NODE_RG}" -n "${INGRESS_PIP_NAME}" --query ipAddress -o tsv)
export PUBIP

############################################
#        Storage secret for service2       #
############################################
# We can (re)create the connection string secret on each run to keep it fresh
STG_ACCOUNT="tfgpdfs"   # must match your Bicep param
log "Creating/Updating K8s secret 'azure-storage-conn' with current connection string"
CONN_STR=$(az storage account show-connection-string -n "${STG_ACCOUNT}" -g "${RESOURCE_GROUP}" --query connectionString -o tsv)
kubectl create secret generic azure-storage-conn \
  --from-literal=AZURE_STORAGE_CONNECTION_STRING="${CONN_STR}" \
  --dry-run=client -o yaml | kubectl apply -f -

############################################
#        Ingress-NGINX (Controller)        #
############################################
if [[ "${INSTALL_INGRESS_NGINX}" == true ]]; then
  log "Installing/Upgrading ingress-nginx (Helm)"
  helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx 1>/dev/null || true
  helm repo update 1>/dev/null
  kubectl get ns ingress-nginx 1>/dev/null 2>&1 || kubectl create ns ingress-nginx

  if ! helm status ingress-nginx -n ingress-nginx 1>/dev/null 2>&1; then
    helm install ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx
  else
    helm upgrade ingress-nginx ingress-nginx/ingress-nginx -n ingress-nginx
  fi

  kubectl rollout status deploy/ingress-nginx-controller -n ingress-nginx --timeout=180s || true

  # Bind Service to our static IP and correct RG
  kubectl -n ingress-nginx annotate svc ingress-nginx-controller \
    service.beta.kubernetes.io/azure-pip-name="${INGRESS_PIP_NAME}" \
    service.beta.kubernetes.io/azure-load-balancer-resource-group="${NODE_RG}" \
    --overwrite

  kubectl -n ingress-nginx patch svc ingress-nginx-controller --type='merge' -p "{\"spec\":{\"loadBalancerIP\":\"${PUBIP}\"}}"

  # Use TCP probe (avoid path issues in Windows Git Bash)
  kubectl -n ingress-nginx annotate svc ingress-nginx-controller \
    service.beta.kubernetes.io/azure-load-balancer-health-probe-protocol="tcp" --overwrite

  # Remove any wrong request-path annotation if present
  kubectl -n ingress-nginx annotate svc ingress-nginx-controller \
    service.beta.kubernetes.io/azure-load-balancer-health-probe-request-path- || true

  # Wait the Service to publish our exact IP
  log "Waiting for ingress external IP (${PUBIP})..."
  for i in {1..40}; do
    EXT_IP=$(kubectl get svc ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)
    [[ "${EXT_IP}" == "${PUBIP}" ]] && break || sleep 5
  done
  log "Ingress IP: ${EXT_IP:-<pending>}"

  # Ensure NSG rule to allow Azure LB -> NodePorts
  NSG_ID=$(az network vnet list -g "${NODE_RG}" --query "[0].subnets[0].networkSecurityGroup.id" -o tsv 2>/dev/null || echo "")
  if [[ -z "${NSG_ID}" || "${NSG_ID}" == "null" ]]; then
    NIC=$(az network nic list -g "${NODE_RG}" --query "[?contains(name,'aks-nodepool')].name" -o tsv | head -n1)
    NSG_ID=$(az network nic show -g "${NODE_RG}" -n "${NIC}" --query "networkSecurityGroup.id" -o tsv)
  fi
  if [[ -n "${NSG_ID}" && "${NSG_ID}" != "null" ]]; then
    NSG_NAME=$(basename "${NSG_ID}")
    NSG_RG=$(echo "${NSG_ID}" | awk -F/ '{for(i=1;i<=NF;i++) if($i=="resourceGroups"){print $(i+1);break}}')
    log "Ensuring NSG rule on ${NSG_NAME} (${NSG_RG}) for NodePorts 30000-32767"
    az network nsg rule create -g "${NSG_RG}" --nsg-name "${NSG_NAME}" \
      -n aks_allow_lb_to_nodeports --priority 300 --access Allow --protocol Tcp \
      --direction Inbound --source-address-prefixes AzureLoadBalancer \
      --destination-port-ranges 30000-32767 --only-show-errors 1>/dev/null || true
  else
    warn "NSG not found; if you see external timeouts, add rule AzureLoadBalancer -> 30000-32767 manually."
  fi
fi

############################################
#        cert-manager (optional)           #
############################################
if [[ "${INSTALL_CERT_MANAGER}" == true ]]; then
  log "Installing cert-manager"
  kubectl get ns cert-manager 1>/dev/null 2>&1 || kubectl create ns cert-manager
  kubectl apply -f "${CERT_MANAGER_MANIFEST}"

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
if [[ "${USE_DOCKER_HUB}" == true ]]; then
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

############################################
#        Apply Kubernetes manifests        #
############################################
log "Applying manifests: FRONTEND"
kubectl apply -f "${FRONTEND_DEP}"
kubectl apply -f "${FRONTEND_SVC}"
[[ -f "${FRONTEND_HPA}" ]] && kubectl apply -f "${FRONTEND_HPA}"
[[ -f "${FRONTEND_PDB}" ]] && kubectl apply -f "${FRONTEND_PDB}"

FRONTEND_HOST="frontend.${PUBIP}.nip.io"
export FRONTEND_HOST
log "Using frontend host: ${FRONTEND_HOST}"
if command -v envsubst >/dev/null 2>&1; then
  envsubst < "${FRONTEND_ING}" | kubectl apply -f -
else
  TMP_ING=$(mktemp)
  sed -E "s/frontend\.[0-9.]+\.nip\.io/${FRONTEND_HOST}/g" "${FRONTEND_ING}" > "${TMP_ING}"
  kubectl apply -f "${TMP_ING}"
  rm -f "${TMP_ING}"
fi

log "Applying manifests: SERVICE1"
kubectl apply -f "${S1_CM}"
kubectl apply -f "${S1_SEC}"
kubectl apply -f "${S1_DEP}"
kubectl apply -f "${S1_SVC}"
[[ -f "${S1_HPA}" ]] && kubectl apply -f "${S1_HPA}"
[[ -f "${S1_PDB}" ]] && kubectl apply -f "${S1_PDB}"

log "Applying manifests: SERVICE2"
kubectl apply -f "${S2_CM}"
kubectl apply -f "${S2_SEC}"
kubectl apply -f "${S2_DEP}"
kubectl apply -f "${S2_SVC}"
[[ -f "${S2_HPA}" ]] && kubectl apply -f "${S2_HPA}"
[[ -f "${S2_PDB}" ]] && kubectl apply -f "${S2_PDB}"

log "Restarting service2 to pick up updated Storage connection string"
kubectl rollout restart deploy/service2-backend

log "Applying manifests: SERVICE3"
kubectl apply -f "${S3_CM}"
kubectl apply -f "${S3_SEC}"
kubectl apply -f "${S3_DEP}"
kubectl apply -f "${S3_SVC}"
[[ -f "${S3_HPA}" ]] && kubectl apply -f "${S3_HPA}"
[[ -f "${S3_PDB}" ]] && kubectl apply -f "${S3_PDB}"

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
log "Bootstrap completed."
echo "-------------------------------------------"
echo "Resource Group:     ${RESOURCE_GROUP}"
echo "AKS:                ${AKS_NAME}"
echo "Ingress LB IP:      ${PUBIP}"
echo "Frontend Host:      frontend.${PUBIP}.nip.io"
echo "-------------------------------------------"
echo "Check status with:"
echo "  kubectl get pods -A"
echo "  kubectl get svc -A"
echo "  kubectl get ingress -A"
echo "  kubectl get hpa,pdb"
echo "-------------------------------------------"
