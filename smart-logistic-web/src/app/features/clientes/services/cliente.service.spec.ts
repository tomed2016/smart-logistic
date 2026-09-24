import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { environment } from '../../../../environments/environment';
import { Cliente, PaginaClientes } from '../models/cliente.model';
import { ClienteService } from './cliente.service';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.customerServiceBaseUrl}/api/v1/clientes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('lista clientes con los parametros de filtro y paginacion', () => {
    const paginaEsperada: PaginaClientes = { contenido: [], totalElementos: 0, pagina: 0, tamanoPagina: 20 };

    service.listar({ comuna: '13119', estado: 'ACTIVO', page: 0, size: 20 }).subscribe((pagina) => {
      expect(pagina).toEqual(paginaEsperada);
    });

    const request = httpMock.expectOne(
      (req) =>
        req.url === baseUrl &&
        req.params.get('comuna') === '13119' &&
        req.params.get('estado') === 'ACTIVO' &&
        req.params.get('page') === '0' &&
        req.params.get('size') === '20'
    );
    expect(request.request.method).toBe('GET');
    request.flush(paginaEsperada);
  });

  it('obtiene un cliente por identificador', () => {
    const clienteEsperado = { id: 'abc-123' } as Cliente;

    service.obtener('abc-123').subscribe((cliente) => {
      expect(cliente).toEqual(clienteEsperado);
    });

    const request = httpMock.expectOne(`${baseUrl}/abc-123`);
    expect(request.request.method).toBe('GET');
    request.flush(clienteEsperado);
  });

  it('crea un cliente', () => {
    const comando = {
      rut: '12345678-5',
      tipoCliente: 'PERSONA_NATURAL' as const,
      nombre: 'Cliente de prueba',
      telefonos: [],
      correos: [],
      condicionPago: 'CONTADO' as const,
      prioridadComercial: 'ESTANDAR' as const
    };

    service.crear(comando).subscribe((respuesta) => {
      expect(respuesta).toEqual({ id: 'nuevo-id' });
    });

    const request = httpMock.expectOne(baseUrl);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(comando);
    request.flush({ id: 'nuevo-id' });
  });

  it('desactiva un cliente', () => {
    service.desactivar('abc-123').subscribe();

    const request = httpMock.expectOne(`${baseUrl}/abc-123`);
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });

  it('agrega una direccion a un cliente', () => {
    const comando = {
      calle: 'Av. Siempre Viva',
      numero: '123',
      codigoComuna: '13119',
      latitud: -33.5,
      longitud: -70.75,
      referencia: null,
      marcarComoPrincipal: true
    };

    service.agregarDireccion('abc-123', comando).subscribe((respuesta) => {
      expect(respuesta).toEqual({ id: 'direccion-1' });
    });

    const request = httpMock.expectOne(`${baseUrl}/abc-123/direcciones`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(comando);
    request.flush({ id: 'direccion-1' });
  });
});
