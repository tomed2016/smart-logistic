/**
 * Modelos del bounded context Clientes, en espejo exacto de los DTOs REST de
 * `customer-service` (`ClienteResponse`, `DireccionResponse`, `CrearClienteRequest`,
 * `ActualizarClienteRequest`, `AgregarDireccionRequest`, `ActualizarDireccionRequest`,
 * `PaginaClientesResponse`). Ver `services/customer-service/.../infrastructure/adapter/in/rest/dto`.
 */

export type TipoCliente = 'PERSONA_NATURAL' | 'EMPRESA';

export const ETIQUETAS_TIPO_CLIENTE: Record<TipoCliente, string> = {
  PERSONA_NATURAL: 'Persona natural',
  EMPRESA: 'Empresa'
};

export type CondicionPago = 'CONTADO' | 'CREDITO_7_DIAS' | 'CREDITO_15_DIAS' | 'CREDITO_30_DIAS';

export const ETIQUETAS_CONDICION_PAGO: Record<CondicionPago, string> = {
  CONTADO: 'Contado',
  CREDITO_7_DIAS: 'Crédito 7 días',
  CREDITO_15_DIAS: 'Crédito 15 días',
  CREDITO_30_DIAS: 'Crédito 30 días'
};

export type PrioridadComercial = 'ESTANDAR' | 'PREFERENTE' | 'VIP';

export const ETIQUETAS_PRIORIDAD_COMERCIAL: Record<PrioridadComercial, string> = {
  ESTANDAR: 'Estándar',
  PREFERENTE: 'Preferente',
  VIP: 'VIP'
};

export type EstadoCliente = 'ACTIVO' | 'INACTIVO';

export const ETIQUETAS_ESTADO_CLIENTE: Record<EstadoCliente, string> = {
  ACTIVO: 'Activo',
  INACTIVO: 'Inactivo'
};

export interface Direccion {
  id: string;
  calle: string;
  numero: string;
  comunaCodigo: string;
  comunaNombre: string;
  /**
   * Nombre de la constante Java del enum `Region` (ej. `METROPOLITANA_DE_SANTIAGO`),
   * no el nombre oficial con tildes. Limitacion conocida del contrato actual de
   * `DireccionResponse`; se muestra formateado vía `EnumALegiblePipe` mientras no se
   * exponga el nombre oficial de la region. `comunaNombre` es la fuente confiable
   * para mostrar la ubicación al usuario.
   */
  region: string;
  latitud: number;
  longitud: number;
  referencia: string | null;
  esPrincipal: boolean;
}

export interface Cliente {
  id: string;
  rut: string;
  tipoCliente: TipoCliente;
  nombre: string;
  telefonos: string[];
  correos: string[];
  condicionPago: CondicionPago;
  prioridadComercial: PrioridadComercial;
  estado: EstadoCliente;
  direcciones: Direccion[];
  creadoEn: string;
  actualizadoEn: string;
}

export interface PaginaClientes {
  contenido: Cliente[];
  totalElementos: number;
  pagina: number;
  tamanoPagina: number;
}

export interface CrearClienteRequest {
  rut: string;
  tipoCliente: TipoCliente;
  nombre: string;
  telefonos: string[];
  correos: string[];
  condicionPago: CondicionPago;
  prioridadComercial: PrioridadComercial;
}

export interface ActualizarClienteRequest {
  nombre: string;
  telefonos: string[];
  correos: string[];
  condicionPago: CondicionPago;
  prioridadComercial: PrioridadComercial;
}

export interface AgregarDireccionRequest {
  calle: string;
  numero: string;
  codigoComuna: string;
  latitud: number;
  longitud: number;
  referencia: string | null;
  marcarComoPrincipal: boolean;
}

export interface ActualizarDireccionRequest {
  calle: string;
  numero: string;
  codigoComuna: string;
  latitud: number;
  longitud: number;
  referencia: string | null;
}

export interface IdResponse {
  id: string;
}

/** Filtros soportados por `GET /api/v1/clientes` (ver `ClienteController.listar`). */
export interface FiltrosListaClientes {
  comuna?: string;
  estado?: EstadoCliente;
  page: number;
  size: number;
}
