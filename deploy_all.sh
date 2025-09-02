#!/bin/bash
set -e
# Despliegue completo de todos los servicios

#echo '🟢 Aplicando k8s/rabbitmq/rabbitmq-cluster.yaml'
#kubectl apply -f k8s/rabbitmq/rabbitmq-cluster.yaml
echo '🟢 Aplicando k8s/service1/secret.yaml'
kubectl apply -f k8s/service1/secret.yaml
echo '🟢 Aplicando k8s/service1/configmap.yaml'
kubectl apply -f k8s/service1/configmap.yaml
echo '🟢 Aplicando k8s/service1/deployment.yaml'
kubectl apply -f k8s/service1/deployment.yaml
echo '🟢 Aplicando k8s/service1/service.yaml'
kubectl apply -f k8s/service1/service.yaml
echo '🟢 Aplicando k8s/service2/secret.yaml'
kubectl apply -f k8s/service2/secret.yaml
echo '🟢 Aplicando k8s/service2/configmap.yaml'
kubectl apply -f k8s/service2/configmap.yaml
echo '🟢 Aplicando k8s/service2/deployment.yaml'
kubectl apply -f k8s/service2/deployment.yaml
echo '🟢 Aplicando k8s/service2/service.yaml'
kubectl apply -f k8s/service2/service.yaml
echo '🟢 Aplicando k8s/service3/secret.yaml'
kubectl apply -f k8s/service3/secret.yaml
echo '🟢 Aplicando k8s/service3/configmap.yaml'
kubectl apply -f k8s/service3/configmap.yaml
echo '🟢 Aplicando k8s/service3/deployment.yaml'
kubectl apply -f k8s/service3/deployment.yaml
echo '🟢 Aplicando k8s/service3/service.yaml'
kubectl apply -f k8s/service3/service.yaml
echo '🟢 Aplicando k8s/frontend/deployment.yaml'
kubectl apply -f k8s/frontend/deployment.yaml
echo '🟢 Aplicando k8s/frontend/service.yaml'
kubectl apply -f k8s/frontend/service.yaml
echo '🟢 Aplicando k8s/frontend/ingress.yaml'
kubectl apply -f k8s/frontend/ingress.yaml
