import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../../environments/environment';
import { GeoCatalogService } from './geo-catalog.service';

describe('GeoCatalogService', () => {
  let service: GeoCatalogService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.geoCatalogServiceBaseUrl}/api/v1`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(GeoCatalogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('lista regiones', () => {
    const regionesEsperadas = [{ codigo: 13, nombre: 'Región Metropolitana de Santiago' }];

    service.listarRegiones().subscribe((regiones) => {
      expect(regiones).toEqual(regionesEsperadas);
    });

    const request = httpMock.expectOne(`${baseUrl}/regiones`);
    expect(request.request.method).toBe('GET');
    request.flush(regionesEsperadas);
  });

  it('lista comunas de una region', () => {
    const comunasEsperadas = [{ codigo: 13119, nombre: 'Maipú', provinciaCodigo: 131, regionCodigo: 13 }];

    service.listarComunasDeRegion(13).subscribe((comunas) => {
      expect(comunas).toEqual(comunasEsperadas);
    });

    const request = httpMock.expectOne(`${baseUrl}/regiones/13/comunas`);
    expect(request.request.method).toBe('GET');
    request.flush(comunasEsperadas);
  });

  it('busca comunas por nombre', () => {
    const comunasEsperadas = [{ codigo: 13119, nombre: 'Maipú', provinciaCodigo: 131, regionCodigo: 13 }];

    service.buscarComunasPorNombre('Maip').subscribe((comunas) => {
      expect(comunas).toEqual(comunasEsperadas);
    });

    const request = httpMock.expectOne((req) => req.url === `${baseUrl}/comunas` && req.params.get('nombre') === 'Maip');
    expect(request.request.method).toBe('GET');
    request.flush(comunasEsperadas);
  });

  it('verifica si una fecha es dia habil', () => {
    const respuestaEsperada = { fecha: '2024-06-02', esDiaHabil: false };

    service.verificarDiaHabil('2024-06-02').subscribe((respuesta) => {
      expect(respuesta).toEqual(respuestaEsperada);
    });

    const request = httpMock.expectOne(
      (req) => req.url === `${baseUrl}/dias-habiles/verificar` && req.params.get('fecha') === '2024-06-02'
    );
    expect(request.request.method).toBe('GET');
    request.flush(respuestaEsperada);
  });
});
