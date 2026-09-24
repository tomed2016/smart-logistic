import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../../../environments/environment';
import { Cliente } from '../../models/cliente.model';
import { ClienteDetallePage } from './cliente-detalle.page';

describe('ClienteDetallePage', () => {
  let httpMock: HttpTestingController;
  const clienteId = 'cliente-1';
  const baseUrl = `${environment.customerServiceBaseUrl}/api/v1/clientes/${clienteId}`;

  const clienteDeEjemplo: Cliente = {
    id: clienteId,
    rut: '12.345.678-5',
    tipoCliente: 'PERSONA_NATURAL',
    nombre: 'Cliente de prueba',
    telefonos: ['+56911111111'],
    correos: ['cliente@ejemplo.cl'],
    condicionPago: 'CONTADO',
    prioridadComercial: 'ESTANDAR',
    estado: 'ACTIVO',
    direcciones: [],
    creadoEn: '2024-01-01T00:00:00Z',
    actualizadoEn: '2024-01-01T00:00:00Z'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteDetallePage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNoopAnimations(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: clienteId }) } }
        }
      ]
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('carga el cliente al inicializar usando el id de la ruta', async () => {
    const fixture = TestBed.createComponent(ClienteDetallePage);
    fixture.detectChanges();

    const request = httpMock.expectOne(baseUrl);
    request.flush(clienteDeEjemplo);
    fixture.detectChanges();
    await fixture.whenStable();

    const texto = (fixture.nativeElement as HTMLElement).textContent ?? '';
    expect(texto).toContain('Cliente de prueba');
    expect(texto).toContain('12.345.678-5');
  });

  it('envía la actualización con los cambios del formulario', async () => {
    const fixture = TestBed.createComponent(ClienteDetallePage);
    fixture.detectChanges();

    httpMock.expectOne(baseUrl).flush(clienteDeEjemplo);
    fixture.detectChanges();
    await fixture.whenStable();

    const componente = fixture.componentInstance as any;
    componente.formulario.controls.nombre.setValue('Nombre actualizado');
    componente.guardarCambios();

    const request = httpMock.expectOne(baseUrl);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toMatchObject({ nombre: 'Nombre actualizado' });
    request.flush({ ...clienteDeEjemplo, nombre: 'Nombre actualizado' });
  });
});
