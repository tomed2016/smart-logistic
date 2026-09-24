import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../../../environments/environment';
import { ClienteCrearPage } from './cliente-crear.page';

describe('ClienteCrearPage', () => {
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.customerServiceBaseUrl}/api/v1/clientes`;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteCrearPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNoopAnimations(),
        provideRouter([{ path: 'clientes/:id', children: [] }])
      ]
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('marca el RUT como invalido cuando el digito verificador no corresponde', () => {
    const fixture = TestBed.createComponent(ClienteCrearPage);
    const componente = fixture.componentInstance as any;

    componente.formulario.controls.rut.setValue('12345678-9');

    expect(componente.formulario.controls.rut.hasError('rutInvalido')).toBe(true);
  });

  it('acepta un RUT valido y no reporta el control como invalido', () => {
    const fixture = TestBed.createComponent(ClienteCrearPage);
    const componente = fixture.componentInstance as any;

    componente.formulario.controls.rut.setValue('12345678-5');
    componente.formulario.controls.nombre.setValue('Cliente de prueba');

    expect(componente.formulario.controls.rut.valid).toBe(true);
  });

  it('envia la solicitud de creacion con los telefonos y correos agregados', () => {
    const fixture = TestBed.createComponent(ClienteCrearPage);
    const componente = fixture.componentInstance as any;

    componente.formulario.controls.rut.setValue('12345678-5');
    componente.formulario.controls.nombre.setValue('Cliente de prueba');
    componente.telefonos.set(['+56912345678']);
    componente.correos.set(['cliente@ejemplo.cl']);

    componente.guardar();

    const request = httpMock.expectOne(baseUrl);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toMatchObject({
      rut: '12345678-5',
      nombre: 'Cliente de prueba',
      telefonos: ['+56912345678'],
      correos: ['cliente@ejemplo.cl']
    });
    request.flush({ id: 'nuevo-cliente-id' });
  });
});
