import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { Router, RouterLink } from '@angular/router';
import { ErrorAplicacion } from '../../../../core/models/error-api.model';
import { EnumALegiblePipe } from '../../../../shared/pipes/enum-a-legible.pipe';
import { Cliente, ETIQUETAS_ESTADO_CLIENTE, ETIQUETAS_TIPO_CLIENTE, EstadoCliente } from '../../models/cliente.model';
import { ClienteService } from '../../services/cliente.service';

/** Página de listado de clientes: tabla paginada del lado del servidor con filtros por estado y comuna. */
@Component({
  selector: 'app-clientes-listado',
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    EnumALegiblePipe
  ],
  templateUrl: './clientes-listado.page.html',
  styleUrl: './clientes-listado.page.scss'
})
export class ClientesListadoPage implements OnInit {
  private readonly clienteService = inject(ClienteService);
  private readonly router = inject(Router);

  protected readonly columnas = ['rut', 'nombre', 'tipoCliente', 'condicionPago', 'prioridadComercial', 'estado'];
  protected readonly etiquetasEstado = ETIQUETAS_ESTADO_CLIENTE;
  protected readonly etiquetasTipo = ETIQUETAS_TIPO_CLIENTE;
  protected readonly opcionesEstado: EstadoCliente[] = ['ACTIVO', 'INACTIVO'];

  protected readonly filtroComuna = new FormControl('');
  protected readonly filtroEstado = new FormControl<EstadoCliente | ''>('');

  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly totalElementos = signal(0);
  protected readonly pagina = signal(0);
  protected readonly tamanoPagina = signal(10);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar();
  }

  protected cambiarPagina(evento: PageEvent): void {
    this.pagina.set(evento.pageIndex);
    this.tamanoPagina.set(evento.pageSize);
    this.cargar();
  }

  protected aplicarFiltros(): void {
    this.pagina.set(0);
    this.cargar();
  }

  protected irADetalle(cliente: Cliente): void {
    this.router.navigate(['/clientes', cliente.id]);
  }

  protected etiquetaTipo(cliente: Cliente): string {
    return this.etiquetasTipo[cliente.tipoCliente];
  }

  protected etiquetaEstado(cliente: Cliente): string {
    return this.etiquetasEstado[cliente.estado];
  }

  private cargar(): void {
    this.cargando.set(true);
    this.mensajeError.set(null);
    const estado = this.filtroEstado.value || undefined;
    const comuna = this.filtroComuna.value?.trim() || undefined;
    this.clienteService
      .listar({ comuna, estado, page: this.pagina(), size: this.tamanoPagina() })
      .subscribe({
        next: (respuesta) => {
          this.clientes.set(respuesta.contenido);
          this.totalElementos.set(respuesta.totalElementos);
          this.cargando.set(false);
        },
        error: (error: ErrorAplicacion) => {
          this.mensajeError.set(error.message);
          this.cargando.set(false);
        }
      });
  }
}
