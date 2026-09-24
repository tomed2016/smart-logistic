import { describe, expect, it } from 'vitest';
import { esRutValido, formatoPresentacionRut, limpiarRut } from './rut.util';

describe('rut.util', () => {
  it('valida un RUT correcto con digito verificador numerico', () => {
    expect(esRutValido('12345678-5')).toBe(true);
    expect(esRutValido('12.345.678-5')).toBe(true);
  });

  it('valida un RUT correcto con digito verificador K', () => {
    // 1.000.005 -> digito verificador K (verificado ejecutando el mismo algoritmo).
    expect(esRutValido('1000005-K')).toBe(true);
    expect(esRutValido('1.000.005-k')).toBe(true);
  });

  it('rechaza un RUT con digito verificador incorrecto', () => {
    expect(esRutValido('12345678-9')).toBe(false);
  });

  it('rechaza un cuerpo no numerico', () => {
    expect(esRutValido('ABCDEFGH-5')).toBe(false);
  });

  it('rechaza valores nulos, vacios o demasiado cortos', () => {
    expect(esRutValido(null)).toBe(false);
    expect(esRutValido(undefined)).toBe(false);
    expect(esRutValido('')).toBe(false);
    expect(esRutValido('123-4')).toBe(false);
  });

  it('limpia puntos, guiones y espacios', () => {
    expect(limpiarRut(' 12.345.678-5 ')).toBe('123456785');
  });

  it('formatea un RUT valido en formato de presentacion', () => {
    expect(formatoPresentacionRut('123456785')).toBe('12.345.678-5');
    expect(formatoPresentacionRut('1000005K')).toBe('1.000.005-K');
  });
});
