import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { ErrorAplicacion } from '../models/error-api.model';
import { erroresHttpInterceptor } from './errores-http.interceptor';

describe('erroresHttpInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(withInterceptors([erroresHttpInterceptor])), provideHttpClientTesting()]
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('normaliza un error con cuerpo {timestamp,status,error,message} de la API', () => {
    let errorCapturado: unknown;

    http.get('/api/v1/clientes/no-existe').subscribe({
      error: (error) => (errorCapturado = error)
    });

    httpMock
      .expectOne('/api/v1/clientes/no-existe')
      .flush(
        { timestamp: '2024-01-01T00:00:00Z', status: 404, error: 'Not Found', message: 'Cliente no encontrado' },
        { status: 404, statusText: 'Not Found' }
      );

    expect(errorCapturado).toBeInstanceOf(ErrorAplicacion);
    const error = errorCapturado as ErrorAplicacion;
    expect(error.status).toBe(404);
    expect(error.message).toBe('Cliente no encontrado');
  });

  it('produce un mensaje generico cuando el servidor no es alcanzable (status 0)', () => {
    let errorCapturado: unknown;

    http.get('/api/v1/clientes').subscribe({
      error: (error) => (errorCapturado = error)
    });

    httpMock.expectOne('/api/v1/clientes').error(new ProgressEvent('error'), { status: 0 });

    expect(errorCapturado).toBeInstanceOf(ErrorAplicacion);
    expect((errorCapturado as ErrorAplicacion).status).toBe(0);
    expect((errorCapturado as ErrorAplicacion).message).toContain('No fue posible contactar al servidor');
  });

  it('produce un mensaje generico cuando el cuerpo de error no trae "message"', () => {
    let errorCapturado: unknown;

    http.get('/api/v1/clientes').subscribe({
      error: (error) => (errorCapturado = error)
    });

    httpMock.expectOne('/api/v1/clientes').flush(null, { status: 500, statusText: 'Internal Server Error' });

    expect((errorCapturado as ErrorAplicacion).message).toContain('HTTP 500');
  });
});
