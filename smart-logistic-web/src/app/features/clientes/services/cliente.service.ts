import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  ActualizarClienteRequest,
  ActualizarDireccionRequest,
  AgregarDireccionRequest,
  Cliente,
  CrearClienteRequest,
  FiltrosListaClientes,
  IdResponse,
  PaginaClientes
} from '../models/cliente.model';

/**
 * Cliente HTTP para el bounded context Clientes (`customer-service`). Encapsula la
 * forma exacta de la API REST (ver `ClienteController`) para que el resto del
 * frontend trabaje solo con los modelos de `cliente.model.ts`.
 */
@Injectable({ providedIn: 'root' })
export class ClienteService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.customerServiceBaseUrl}/api/v1/clientes`;

  listar(filtros: FiltrosListaClientes): Observable<PaginaClientes> {
    let params = new HttpParams().set('page', filtros.page).set('size', filtros.size);
    if (filtros.comuna) {
      params = params.set('comuna', filtros.comuna);
    }
    if (filtros.estado) {
      params = params.set('estado', filtros.estado);
    }
    return this.http.get<PaginaClientes>(this.baseUrl, { params });
  }

  obtener(clienteId: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${clienteId}`);
  }

  crear(request: CrearClienteRequest): Observable<IdResponse> {
    return this.http.post<IdResponse>(this.baseUrl, request);
  }

  actualizar(clienteId: string, request: ActualizarClienteRequest): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${clienteId}`, request);
  }

  desactivar(clienteId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${clienteId}`);
  }

  agregarDireccion(clienteId: string, request: AgregarDireccionRequest): Observable<IdResponse> {
    return this.http.post<IdResponse>(`${this.baseUrl}/${clienteId}/direcciones`, request);
  }

  actualizarDireccion(
    clienteId: string,
    direccionId: string,
    request: ActualizarDireccionRequest
  ): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.baseUrl}/${clienteId}/direcciones/${direccionId}`, request);
  }
}
