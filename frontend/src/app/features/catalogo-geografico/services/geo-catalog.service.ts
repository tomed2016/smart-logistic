import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Comuna, DiaHabil, Region } from '../models/geo.model';

/**
 * Cliente HTTP para el servicio de referencia Catalogo Geografico CL
 * (`geo-catalog-service`, ver `GeoCatalogController`). Todos los endpoints
 * consumidos son de solo lectura.
 */
@Injectable({ providedIn: 'root' })
export class GeoCatalogService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.geoCatalogServiceBaseUrl}/api/v1`;

  listarRegiones(): Observable<Region[]> {
    return this.http.get<Region[]>(`${this.baseUrl}/regiones`);
  }

  listarComunasDeRegion(codigoRegion: number): Observable<Comuna[]> {
    return this.http.get<Comuna[]>(`${this.baseUrl}/regiones/${codigoRegion}/comunas`);
  }

  buscarComunasPorNombre(nombre: string): Observable<Comuna[]> {
    const params = new HttpParams().set('nombre', nombre);
    return this.http.get<Comuna[]>(`${this.baseUrl}/comunas`, { params });
  }

  buscarComuna(codigo: number): Observable<Comuna> {
    return this.http.get<Comuna>(`${this.baseUrl}/comunas/${codigo}`);
  }

  verificarDiaHabil(fechaIso: string): Observable<DiaHabil> {
    const params = new HttpParams().set('fecha', fechaIso);
    return this.http.get<DiaHabil>(`${this.baseUrl}/dias-habiles/verificar`, { params });
  }
}
