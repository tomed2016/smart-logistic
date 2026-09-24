/**
 * Modelos del servicio de referencia Catalogo Geografico CL, en espejo de los DTOs
 * REST de `geo-catalog-service` (`RegionResponse`, `ComunaResponse`, `DiaHabilResponse`).
 * Todos los endpoints de origen son de solo lectura.
 */

export interface Region {
  codigo: number;
  nombre: string;
}

export interface Comuna {
  codigo: number;
  nombre: string;
  provinciaCodigo: number;
  regionCodigo: number;
}

export interface DiaHabil {
  fecha: string;
  esDiaHabil: boolean;
}
