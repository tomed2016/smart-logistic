/**
 * Configuracion de entorno para desarrollo local (`ng serve`, `ng build --configuration development`).
 * Apunta directamente a los puertos por defecto expuestos por `infra/docker-compose.yml`
 * cuando se ejecuta en la maquina del desarrollador (ver README raiz del proyecto).
 */
export const environment = {
  production: false,
  customerServiceBaseUrl: 'http://localhost:8081',
  geoCatalogServiceBaseUrl: 'http://localhost:8082'
};
