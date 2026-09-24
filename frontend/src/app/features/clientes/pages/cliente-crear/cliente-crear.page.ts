import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule, MatChipInputEvent } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router, RouterLink } from '@angular/router';
import { ErrorAplicacion } from '../../../../core/models/error-api.model';
import { rutValidator } from '../../../../core/validators/rut.validator';
import {
  CondicionPago,
  ETIQUETAS_CONDICION_PAGO,
  ETIQUETAS_PRIORIDAD_COMERCIAL,
  ETIQUETAS_TIPO_CLIENTE,
  PrioridadComercial,
  TipoCliente
} from '../../models/cliente.model';
import { ClienteService } from '../../services/cliente.service';

/** Página de creación de un nuevo cliente mediante un formulario reactivo. */
@Component({
  selector: 'app-cliente-crear',
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MatButtonModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './cliente-crear.page.html',
  styleUrl: './cliente-crear.page.scss'
})
export class ClienteCrearPage {
  private readonly formBuilder = inject(FormBuilder);
  private readonly clienteService = inject(ClienteService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly etiquetasTipo = ETIQUETAS_TIPO_CLIENTE;
  protected readonly etiquetasCondicionPago = ETIQUETAS_CONDICION_PAGO;
  protected readonly etiquetasPrioridad = ETIQUETAS_PRIORIDAD_COMERCIAL;

  protected readonly opcionesTipo: TipoCliente[] = ['PERSONA_NATURAL', 'EMPRESA'];
  protected readonly opcionesCondicionPago: CondicionPago[] = [
    'CONTADO',
    'CREDITO_7_DIAS',
    'CREDITO_15_DIAS',
    'CREDITO_30_DIAS'
  ];
  protected readonly opcionesPrioridad: PrioridadComercial[] = ['ESTANDAR', 'PREFERENTE', 'VIP'];

  protected readonly telefonos = signal<string[]>([]);
  protected readonly correos = signal<string[]>([]);
  protected readonly guardando = signal(false);
  protected readonly mensajeError = signal<string | null>(null);

  protected readonly formulario = this.formBuilder.nonNullable.group({
    rut: ['', [Validators.required, rutValidator()]],
    tipoCliente: this.formBuilder.nonNullable.control<TipoCliente>('PERSONA_NATURAL', Validators.required),
    nombre: ['', Validators.required],
    condicionPago: this.formBuilder.nonNullable.control<CondicionPago>('CONTADO', Validators.required),
    prioridadComercial: this.formBuilder.nonNullable.control<PrioridadComercial>('ESTANDAR', Validators.required)
  });

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

  protected guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.guardando.set(true);
    this.mensajeError.set(null);
    const valores = this.formulario.getRawValue();
    this.clienteService
      .crear({
        rut: valores.rut,
        tipoCliente: valores.tipoCliente,
        nombre: valores.nombre,
        telefonos: this.telefonos(),
        correos: this.correos(),
        condicionPago: valores.condicionPago,
        prioridadComercial: valores.prioridadComercial
      })
      .subscribe({
        next: (respuesta) => {
          this.guardando.set(false);
          this.snackBar.open('Cliente creado correctamente', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/clientes', respuesta.id]);
        },
        error: (error: ErrorAplicacion) => {
          this.guardando.set(false);
          this.mensajeError.set(error.message);
        }
      });
  }
}
