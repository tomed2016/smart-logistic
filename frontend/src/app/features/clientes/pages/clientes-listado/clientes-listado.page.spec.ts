import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../../../environments/environment';
import { erroresHttpInterceptor } from '../../../../core/http/errores-http.interceptor';
import { PaginaClientes } from '../../models/cliente.model';
import { ClientesListadoPage } from './clientes-listado.page';

describe('ClientesListadoPage', () => {
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.customerServiceBaseUrl}/api/v1/clientes`;

  const paginaDeEjemplo: PaginaClientes = {
    contenido: [
      {
        id: 'cliente-1',
        rut: '12.345.678-5',
        tipoCliente: 'PERSONA_NATURAL',
        nombre: 'Cliente de prueba',
        telefonos: [],
        correos: [],
        condicionPago: 'CONTADO',
        prioridadComercial: 'ESTANDAR',
        estado: 'ACTIVO',
        direcciones: [],
        creadoEn: '2024-01-01T00:00:00Z',
        actualizadoEn: '2024-01-01T00:00:00Z'
      }
    ],
    totalElementos: 1,
    pagina: 0,
    tamanoPagina: 10
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClientesListadoPage],
      providers: [
        provideHttpClient(withInterceptors([erroresHttpInterceptor])),
        provideHttpClientTesting(),
        provideNoopAnimations(),
        provideRouter([])
      ]
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('carga y muestra los clientes al inicializar', async () => {
    const fixture = TestBed.createComponent(ClientesListadoPage);
    fixture.detectChanges();

    const request = httpMock.expectOne((req) => req.url === baseUrl);
    request.flush(paginaDeEjemplo);
    fixture.detectChanges();
    await fixture.whenStable();

    const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(texto).toContain('Cliente de prueba');
    expect(texto).toContain('12.345.678-5');
  });

  it('muestra el mensaje de error normalizado cuando la API falla', async () => {
    const fixture = TestBed.createComponent(ClientesListadoPage);
    fixture.detectChanges();

    const request = httpMock.expectOne((req) => req.url === baseUrl);
    request.flush(
      { timestamp: '2024-01-01T00:00:00Z', status: 500, error: 'Internal Server Error', message: 'Fallo interno' },
      { status: 500, statusText: 'Internal Server Error' }
    );
    fixture.detectChanges();
    await fixture.whenStable();

    const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(texto).toContain('Fallo interno');
  });
});
