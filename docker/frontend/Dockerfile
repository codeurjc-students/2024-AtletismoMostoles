# Stage 1: Build Angular application
FROM node:20 AS build-stage

WORKDIR /app
COPY frontend/ ./

# Install dependencies and build the project in production mode
RUN npm install
RUN npm run build -- --configuration=production

# Stage 2: Serve with NGINX over HTTPS
FROM nginx:stable-alpine AS production-stage

# Copy compiled Angular files
COPY --from=build-stage /app/dist/frontend/browser /usr/share/nginx/html

# Copy custom NGINX configuration
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

# Copy self-signed SSL certificates
COPY docker/frontend/certs/selfsigned.crt /etc/nginx/certs/selfsigned.crt
COPY docker/frontend/certs/selfsigned.key /etc/nginx/certs/selfsigned.key

# Expose HTTPS port
EXPOSE 443

# Start NGINX in the foreground
CMD ["nginx", "-g", "daemon off;"]
