# Etapa 1: build de la aplicación Angular con Node.
FROM node:22-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx ng build --configuration production

# Etapa 2: servir los estáticos con Nginx, actuando también como reverse proxy
# hacia customer-service y geo-catalog-service (ver nginx.conf). Esto evita CORS
# y evita exponer los servicios backend directamente al navegador.
FROM nginx:1.27-alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
