# Stage 1: Build Angular app
FROM node:18-alpine AS build
WORKDIR /app

# Copy only the frontend project
COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./

RUN npx ng build --configuration production --project frontend

# Stage 2: Serve with NGINX
FROM nginx:1.25-alpine

COPY docker/frontend/nginx.conf /etc/nginx/nginx.conf

COPY --from=build /app/dist/frontend/browser/ /usr/share/nginx/html/

EXPOSE 80

