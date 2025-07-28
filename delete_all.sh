#!/bin/bash

echo "🧨 Deleting frontend (Ingress, Service, Deployment)..."
kubectl delete -f k8s/frontend/ingress.yaml --ignore-not-found
kubectl delete -f k8s/frontend/service.yaml --ignore-not-found
kubectl delete -f k8s/frontend/deployment.yaml --ignore-not-found
kubectl delete secret frontend-ssl-secret --ignore-not-found

echo "🧹 Deleting service3-backend..."
kubectl delete -f k8s/service3/service.yaml --ignore-not-found
kubectl delete -f k8s/service3/deployment.yaml --ignore-not-found
kubectl delete -f k8s/service3/configmap.yaml --ignore-not-found
kubectl delete -f k8s/service3/secret.yaml --ignore-not-found

echo "🧹 Deleting service2-backend..."
kubectl delete -f k8s/service2/service.yaml --ignore-not-found
kubectl delete -f k8s/service2/deployment.yaml --ignore-not-found
kubectl delete -f k8s/service2/configmap.yaml --ignore-not-found
kubectl delete -f k8s/service2/secret.yaml --ignore-not-found

echo "🧹 Deleting service1-backend..."
kubectl delete -f k8s/service1/service.yaml --ignore-not-found
kubectl delete -f k8s/service1/deployment.yaml --ignore-not-found
kubectl delete -f k8s/service1/configmap.yaml --ignore-not-found
kubectl delete -f k8s/service1/secret.yaml --ignore-not-found

echo "🗑️ Deleting RabbitMQ..."
kubectl delete -f k8s/rabbitmq/service.yaml --ignore-not-found
kubectl delete -f k8s/rabbitmq/deployment.yaml --ignore-not-found
kubectl delete -f k8s/rabbitmq/pvc.yaml --ignore-not-found

echo "🗑️ Deleting MySQL databases..."
kubectl delete -f k8s/databases/mysql-service3.yaml --ignore-not-found
kubectl delete -f k8s/databases/mysql-service2.yaml --ignore-not-found
kubectl delete -f k8s/databases/mysql-service1.yaml --ignore-not-found

echo "✅ All resources deleted."