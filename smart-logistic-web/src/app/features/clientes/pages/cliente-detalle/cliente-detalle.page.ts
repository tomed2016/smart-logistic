import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule, MatChipInputEvent } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ErrorAplicacion } from '../../../../core/models/error-api.model';
import { EnumALegiblePipe } from '../../../../shared/pipes/enum-a-legible.pipe';
import { DireccionFormDialog } from '../../components/direccion-form-dialog/direccion-form-dialog';
import {
  AgregarDireccionRequest,
  Cliente,
  CondicionPago,
  ETIQUETAS_CONDICION_PAGO,
  ETIQUETAS_ESTADO_CLIENTE,
  ETIQUETAS_PRIORIDAD_COMERCIAL,
  ETIQUETAS_TIPO_CLIENTE,
  PrioridadComercial
} from '../../models/cliente.model';
import { ClienteService } from '../../services/cliente.service';

/** Página de detalle de un cliente: datos generales editables, direcciones y acciones. */
@Component({
  selector: 'app-cliente-detalle',
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatButtonModule,
    MatChipsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule,
    EnumALegiblePipe
  ],
  templateUrl: './cliente-detalle.page.html',
  styleUrl: './cliente-detalle.page.scss'
})
export class ClienteDetallePage implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly clienteService = inject(ClienteService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly etiquetasTipo = ETIQUETAS_TIPO_CLIENTE;
  protected readonly etiquetasCondicionPago = ETIQUETAS_CONDICION_PAGO;
  protected readonly etiquetasPrioridad = ETIQUETAS_PRIORIDAD_COMERCIAL;
  protected readonly etiquetasEstado = ETIQUETAS_ESTADO_CLIENTE;

  protected readonly opcionesCondicionPago: CondicionPago[] = [
    'CONTADO',
    'CREDITO_7_DIAS',
    'CREDITO_15_DIAS',
    'CREDITO_30_DIAS'
  ];
  protected readonly opcionesPrioridad: PrioridadComercial[] = ['ESTANDAR', 'PREFERENTE', 'VIP'];

  protected readonly cliente = signal<Cliente | null>(null);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);
  protected readonly telefonos = signal<string[]>([]);
  protected readonly correos = signal<string[]>([]);

  protected readonly formulario = this.formBuilder.nonNullable.group({
    nombre: ['', Validators.required],
    condicionPago: this.formBuilder.nonNullable.control<CondicionPago>('CONTADO', Validators.required),
    prioridadComercial: this.formBuilder.nonNullable.control<PrioridadComercial>('ESTANDAR', Validators.required)
  });

  ngOnInit(): void {
    const clienteId = this.route.snapshot.paramMap.get('id');
    if (clienteId) {
      this.cargar(clienteId);
    }
  }

  protected agregarTelefono(evento: MatChipInputEvent): void {
    const valor = (evento.value ?? '').trim();
    if (valor) {
      this.telefonos.update((actuales) => [...actuales, valor]);
    }
    evento.chipInput.clear();
  }

  protected quitarTelefono(telefono: string): void {
    this.telefonos.update((actuales) => actuales.filter((t) => t !== telefono));
  }

  protected agregarCorreo(evento: MatChipInputEvent): void {
    const valor = (evento.value ?? '').trim();
    if (valor) {
      this.correos.update((actuales) => [...actuales, valor]);
    }
    evento.chipInput.clear();
  }

  protected quitarCorreo(correo: string): void {
    this.correos.update((actuales) => actuales.filter((c) => c !== correo));
  }

  protected guardarCambios(): void {
    const cliente = this.cliente();
    if (!cliente || this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.mensajeError.set(null);
    const valores = this.formulario.getRawValue();
    this.clienteService
      .actualizar(cliente.id, {
        nombre: valores.nombre,
        telefonos: this.telefonos(),
        correos: this.correos(),
        condicionPago: valores.condicionPago,
        prioridadComercial: valores.prioridadComercial
      })
      .subscribe({
        next: (actualizado) => {
          this.cliente.set(actualizado);
          this.guardando.set(false);
          this.snackBar.open('Cliente actualizado correctamente', 'Cerrar', { duration: 3000 });
        },
        error: (error: ErrorAplicacion) => {
          this.guardando.set(false);
          this.mensajeError.set(error.message);
        }
      });
  }

  protected desactivar(): void {
    const cliente = this.cliente();
    if (!cliente) {
      return;
    }
    this.clienteService.desactivar(cliente.id).subscribe({
      next: () => {
        this.snackBar.open('Cliente desactivado', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/clientes']);
      },
      error: (error: ErrorAplicacion) => this.mensajeError.set(error.message)
    });
  }

  protected abrirDialogoDireccion(): void {
    const cliente = this.cliente();
    if (!cliente) {
      return;
    }
    const referenciaDialogo = this.dialog.open(DireccionFormDialog, { width: '480px' });
    referenciaDialogo.afterClosed().subscribe((resultado: AgregarDireccionRequest | undefined) => {
      if (resultado) {
        this.agregarDireccion(cliente.id, resultado);
      }
    });
  }

  private agregarDireccion(clienteId: string, request: AgregarDireccionRequest): void {
    this.clienteService.agregarDireccion(clienteId, request).subscribe({
      next: () => {
        this.snackBar.open('Dirección agregada correctamente', 'Cerrar', { duration: 3000 });
        this.cargar(clienteId);
      },
      error: (error: ErrorAplicacion) => this.mensajeError.set(error.message)
    });
  }

  private cargar(clienteId: string): void {
    this.cargando.set(true);
    this.mensajeError.set(null);
    this.clienteService.obtener(clienteId).subscribe({
      next: (cliente) => {
        this.cliente.set(cliente);
        this.telefonos.set([...cliente.telefonos]);
        this.correos.set([...cliente.correos]);
        this.formulario.setValue({
          nombre: cliente.nombre,
          condicionPago: cliente.condicionPago,
          prioridadComercial: cliente.prioridadComercial
        });
        this.cargando.set(false);
      },
      error: (error: ErrorAplicacion) => {
        this.mensajeError.set(error.message);
        this.cargando.set(false);
      }
    });
  }
}
