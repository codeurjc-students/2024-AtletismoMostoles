#!/bin/bash

echo "🚀 Creating SSL Secret for frontend..."
kubectl delete secret frontend-ssl-secret --ignore-not-found
kubectl create secret generic frontend-ssl-secret \
  --from-file=selfsigned.crt=certs/frontend/frontend.crt \
  --from-file=selfsigned.key=certs/frontend/frontend.key

echo "🗄️ Deploying MySQL databases..."
kubectl apply -f k8s/databases/mysql-service1.yaml
kubectl apply -f k8s/databases/mysql-service2.yaml
kubectl apply -f k8s/databases/mysql-service3.yaml

echo "📡 Deploying RabbitMQ..."
kubectl apply -f k8s/rabbitmq/pvc.yaml
kubectl apply -f k8s/rabbitmq/deployment.yaml
kubectl apply -f k8s/rabbitmq/service.yaml

echo "🧠 Deploying service1-backend..."
kubectl apply -f k8s/service1/configmap.yaml
kubectl apply -f k8s/service1/secret.yaml
kubectl apply -f k8s/service1/deployment.yaml
kubectl apply -f k8s/service1/service.yaml

echo "📊 Deploying service2-backend..."
kubectl apply -f k8s/service2/configmap.yaml
kubectl apply -f k8s/service2/secret.yaml
kubectl apply -f k8s/service2/deployment.yaml
kubectl apply -f k8s/service2/service.yaml

echo "📅 Deploying service3-backend..."
kubectl apply -f k8s/service3/configmap.yaml
kubectl apply -f k8s/service3/secret.yaml
kubectl apply -f k8s/service3/deployment.yaml
kubectl apply -f k8s/service3/service.yaml

echo "🌐 Deploying frontend..."
kubectl apply -f k8s/frontend/deployment.yaml
kubectl apply -f k8s/frontend/service.yaml

echo "🧭 Deploying Ingress (if applicable)..."
kubectl apply -f k8s/frontend/ingress.yaml

echo "✅ All components deployed successfully."
