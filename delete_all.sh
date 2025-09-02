#!/bin/bash
set -e
# Eliminación completa de todos los recursos

echo '🧹 Eliminando k8s/frontend/ingress.yaml'
kubectl delete -f k8s/frontend/ingress.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/frontend/service.yaml'
kubectl delete -f k8s/frontend/service.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/frontend/deployment.yaml'
kubectl delete -f k8s/frontend/deployment.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service3/service.yaml'
kubectl delete -f k8s/service3/service.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service3/deployment.yaml'
kubectl delete -f k8s/service3/deployment.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service3/configmap.yaml'
kubectl delete -f k8s/service3/configmap.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service3/secret.yaml'
kubectl delete -f k8s/service3/secret.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service2/service.yaml'
kubectl delete -f k8s/service2/service.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service2/deployment.yaml'
kubectl delete -f k8s/service2/deployment.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service2/configmap.yaml'
kubectl delete -f k8s/service2/configmap.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service2/secret.yaml'
kubectl delete -f k8s/service2/secret.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service1/service.yaml'
kubectl delete -f k8s/service1/service.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service1/deployment.yaml'
kubectl delete -f k8s/service1/deployment.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service1/configmap.yaml'
kubectl delete -f k8s/service1/configmap.yaml --ignore-not-found=true
echo '🧹 Eliminando k8s/service1/secret.yaml'
kubectl delete -f k8s/service1/secret.yaml --ignore-not-found=true
#echo '🧹 Eliminando k8s/rabbitmq/rabbitmq-cluster.yaml'
#kubectl delete -f k8s/rabbitmq/rabbitmq-cluster.yaml --ignore-not-found=true
