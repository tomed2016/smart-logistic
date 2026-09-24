/**
 * Forma del cuerpo de error que exponen tanto `customer-service` como
 * `geo-catalog-service` (ver `GlobalExceptionHandler` en ambos servicios):
 * `{ timestamp, status, error, message }`. Se normaliza aqui para que el resto del
 * frontend no dependa del detalle de transporte HTTP.
 */
export interface ErrorApi {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}

/** Error de aplicacion normalizado, producido por {@link erroresHttpInterceptor}. */
export class ErrorAplicacion extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly detalleOriginal?: ErrorApi
  ) {
    super(message);
    this.name = 'ErrorAplicacion';
  }
}
