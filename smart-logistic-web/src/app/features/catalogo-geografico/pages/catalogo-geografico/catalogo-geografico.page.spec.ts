import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../../../environments/environment';
import { Comuna, Region } from '../../models/geo.model';
import { CatalogoGeograficoPage } from './catalogo-geografico.page';

describe('CatalogoGeograficoPage', () => {
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.geoCatalogServiceBaseUrl}/api/v1`;

  const regionesDeEjemplo: Region[] = [{ codigo: 13, nombre: 'Metropolitana de Santiago' }];
  const comunasDeEjemplo: Comuna[] = [{ codigo: 13119, nombre: 'Providencia', provinciaCodigo: 131, regionCodigo: 13 }];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogoGeograficoPage],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideNoopAnimations()]
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('carga las regiones al inicializar', () => {
    const fixture = TestBed.createComponent(CatalogoGeograficoPage);
    fixture.detectChanges();

    const request = httpMock.expectOne(`${baseUrl}/regiones`);
    request.flush(regionesDeEjemplo);

    const componente = fixture.componentInstance as any;
    expect(componente.regiones()).toEqual(regionesDeEjemplo);
  });

  it('carga las comunas de la región seleccionada', () => {
    const fixture = TestBed.createComponent(CatalogoGeograficoPage);
    fixture.detectChanges();
    httpMock.expectOne(`${baseUrl}/regiones`).flush(regionesDeEjemplo);

    const componente = fixture.componentInstance as any;
    componente.regionSeleccionada.setValue(13);
    componente.seleccionarRegion();

    const request = httpMock.expectOne(`${baseUrl}/regiones/13/comunas`);
    request.flush(comunasDeEjemplo);

    expect(componente.comunas()).toEqual(comunasDeEjemplo);
  });

  it('verifica si una fecha es día hábil', () => {
    const fixture = TestBed.createComponent(CatalogoGeograficoPage);
    fixture.detectChanges();
    httpMock.expectOne(`${baseUrl}/regiones`).flush(regionesDeEjemplo);

    const componente = fixture.componentInstance as any;
    componente.fechaAVerificar.setValue(new Date(2024, 0, 1));
    componente.verificarDiaHabil();

    const request = httpMock.expectOne((req) => req.url === `${baseUrl}/dias-habiles/verificar`);
    expect(request.request.params.get('fecha')).toBe('2024-01-01');
    request.flush({ fecha: '2024-01-01', esDiaHabil: false });

    expect(componente.resultadoDiaHabil()).toEqual({ fecha: '2024-01-01', esDiaHabil: false });
  });
});
