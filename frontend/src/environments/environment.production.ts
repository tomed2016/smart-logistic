/**
 * Configuracion de entorno para el build de produccion (`ng build`, usado por el
 * Dockerfile de este modulo). Las URLs quedan relativas ('') porque en produccion el
 * frontend se sirve detras de un reverse proxy Nginx (ver `frontend/nginx.conf`) que
 * enruta `/api/v1/clientes/**` hacia `customer-service` y el resto de `/api/v1/**`
 * (regiones, provincias, comunas, feriados, dias-habiles) hacia `geo-catalog-service`,
 * ambos en la misma red Docker. Esto evita CORS y evita exponer los servicios backend
 * directamente al navegador del usuario final.
 */
export const environment = {
  production: true,
  customerServiceBaseUrl: '',
  geoCatalogServiceBaseUrl: ''
};
