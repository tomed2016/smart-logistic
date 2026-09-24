/**
 * Algoritmo oficial del Servicio de Impuestos Internos (SII) de Chile para el digito
 * verificador de un RUT (modulo 11, multiplicadores 2-7 ciclicos). Es una replica
 * deliberada, en TypeScript, del algoritmo implementado en
 * `shared-kernel`'s `cl.smartlogistic.shared.rut.Rut` (Java): existe para dar
 * retroalimentacion inmediata en el formulario (fail fast en el navegador), pero el
 * backend sigue siendo la unica fuente de verdad — toda validacion aqui es
 * redundante y no reemplaza la validacion del servidor.
 */

const CARACTERES_A_LIMPIAR = /[.\-\s]/g;
const CUERPO_NUMERICO = /^\d{7,8}$/;

export function limpiarRut(rutTexto: string): string {
  return rutTexto.trim().toUpperCase().replace(CARACTERES_A_LIMPIAR, '');
}

function calcularDigitoVerificador(numero: number): string {
  let suma = 0;
  let multiplicador = 2;
  let resto = numero;
  while (resto > 0) {
    suma += (resto % 10) * multiplicador;
    resto = Math.floor(resto / 10);
    multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
  }
  const digitoCalculado = 11 - (suma % 11);
  if (digitoCalculado === 11) {
    return '0';
  }
  if (digitoCalculado === 10) {
    return 'K';
  }
  return String(digitoCalculado);
}

/** Verifica formato y digito verificador de un RUT en cualquier formato usual chileno. */
export function esRutValido(rutTexto: string | null | undefined): boolean {
  if (!rutTexto) {
    return false;
  }
  const limpio = limpiarRut(rutTexto);
  if (limpio.length < 8 || limpio.length > 9) {
    return false;
  }
  const cuerpo = limpio.substring(0, limpio.length - 1);
  const digitoVerificadorIngresado = limpio.charAt(limpio.length - 1);
  if (!CUERPO_NUMERICO.test(cuerpo)) {
    return false;
  }
  return digitoVerificadorIngresado === calcularDigitoVerificador(Number(cuerpo));
}

/** Formatea un RUT ya validado al formato de presentacion `12.345.678-5`. */
export function formatoPresentacionRut(rutTexto: string): string {
  const limpio = limpiarRut(rutTexto);
  const cuerpo = limpio.substring(0, limpio.length - 1);
  const digitoVerificador = limpio.charAt(limpio.length - 1);
  const conPuntos = cuerpo.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  return `${conPuntos}-${digitoVerificador}`;
}
