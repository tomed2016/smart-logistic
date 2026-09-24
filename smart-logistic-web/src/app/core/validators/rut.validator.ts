import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { esRutValido } from './rut.util';

/**
 * Validador de Reactive Forms para RUT chileno. Deliberadamente no valida
 * "requerido" (eso es responsabilidad de `Validators.required`, compuesto junto a
 * este validador): un campo vacio se considera valido aqui para no duplicar el
 * mensaje de error de campo obligatorio.
 */
export function rutValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value as string | null;
    if (!valor || !valor.trim()) {
      return null;
    }
    return esRutValido(valor) ? null : { rutInvalido: true };
  };
}
