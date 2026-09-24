import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ErrorApi, ErrorAplicacion } from '../models/error-api.model';

/**
 * Interceptor funcional que normaliza cualquier respuesta de error HTTP proveniente
 * de `customer-service` o `geo-catalog-service` en un {@link ErrorAplicacion} con un
 * mensaje legible, para que los componentes no tengan que conocer la forma del
 * cuerpo de error `{ timestamp, status, error, message }` de la API.
 *
 * No intercepta errores 2xx (no aplica) ni intenta reintentar la peticion: la
 * politica de reintentos ante fallas transitorias vive en el backend (Resilience4j,
 * ver ADR 05), no en el navegador.
 */
export const erroresHttpInterceptor: HttpInterceptorFn = (request, next) =>
  next(request).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        return throwError(() => aErrorAplicacion(error));
      }
      return throwError(() => error);
    })
  );

function aErrorAplicacion(error: HttpErrorResponse): ErrorAplicacion {
  const cuerpo = error.error as Partial<ErrorApi> | null;
  if (error.status === 0) {
    return new ErrorAplicacion(
      'No fue posible contactar al servidor. Verifique su conexion e intentelo nuevamente.',
      0
    );
  }
  const mensaje = cuerpo?.message?.trim() || `Error inesperado del servidor (HTTP ${error.status}).`;
  return new ErrorAplicacion(mensaje, error.status, cuerpo as ErrorApi | undefined);
}
