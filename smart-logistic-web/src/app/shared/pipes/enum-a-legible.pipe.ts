import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formatea un identificador `SNAKE_CASE` (ej. `METROPOLITANA_DE_SANTIAGO`,
 * `CREDITO_30_DIAS`) a un texto en Título Case separado por espacios
 * (`Metropolitana De Santiago`). Se usa como respaldo generico para el campo
 * `Direccion.region`, que expone el nombre de la constante del enum Java `Region`
 * en vez de su nombre oficial con tildes (ver nota en `cliente.model.ts`); para el
 * resto de los enums de negocio (tipo de cliente, condicion de pago, prioridad
 * comercial, estado) se usan los mapas `ETIQUETAS_*`, que sí tienen tildes correctas.
 */
@Pipe({ name: 'enumALegible' })
export class EnumALegiblePipe implements PipeTransform {
  transform(valor: string | null | undefined): string {
    if (!valor) {
      return '';
    }
    return valor
      .toLowerCase()
      .split('_')
      .map((palabra) => palabra.charAt(0).toUpperCase() + palabra.slice(1))
      .join(' ');
  }
}
