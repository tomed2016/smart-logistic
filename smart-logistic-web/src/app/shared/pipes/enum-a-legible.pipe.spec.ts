import { describe, expect, it } from 'vitest';
import { EnumALegiblePipe } from './enum-a-legible.pipe';

describe('EnumALegiblePipe', () => {
  const pipe = new EnumALegiblePipe();

  it('convierte SNAKE_CASE a Titulo Case', () => {
    expect(pipe.transform('METROPOLITANA_DE_SANTIAGO')).toBe('Metropolitana De Santiago');
    expect(pipe.transform('CONTADO')).toBe('Contado');
  });

  it('retorna cadena vacia para valores nulos o vacios', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
    expect(pipe.transform('')).toBe('');
  });
});
