import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { ErrorAplicacion } from '../../../../core/models/error-api.model';
import { Comuna, DiaHabil, Region } from '../../models/geo.model';
import { GeoCatalogService } from '../../services/geo-catalog.service';

/**
 * Página de exploración del catálogo geográfico chileno: selector de región con
 * sus comunas, y un verificador de día hábil (considera fines de semana y
 * feriados chilenos, ver `geo-catalog-service`).
 */
@Component({
  selector: 'app-catalogo-geografico',
  providers: [provideNativeDateAdapter()],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTableModule
  ],
  templateUrl: './catalogo-geografico.page.html',
  styleUrl: './catalogo-geografico.page.scss'
})
export class CatalogoGeograficoPage implements OnInit {
  private readonly geoCatalogService = inject(GeoCatalogService);

  protected readonly columnasComunas = ['codigo', 'nombre'];
  protected readonly regiones = signal<Region[]>([]);
  protected readonly comunas = signal<Comuna[]>([]);
  protected readonly cargandoRegiones = signal(false);
  protected readonly cargandoComunas = signal(false);
  protected readonly mensajeError = signal<string | null>(null);

  protected readonly regionSeleccionada = new FormControl<number | null>(null, Validators.required);
  protected readonly fechaAVerificar = new FormControl<Date | null>(null);
  protected readonly resultadoDiaHabil = signal<DiaHabil | null>(null);
  protected readonly verificandoDiaHabil = signal(false);

  ngOnInit(): void {
    this.cargandoRegiones.set(true);
    this.geoCatalogService.listarRegiones().subscribe({
      next: (regiones) => {
        this.regiones.set(regiones);
        this.cargandoRegiones.set(false);
      },
      error: (error: ErrorAplicacion) => {
        this.mensajeError.set(error.message);
        this.cargandoRegiones.set(false);
      }
    });
  }

  protected seleccionarRegion(): void {
    const codigoRegion = this.regionSeleccionada.value;
    if (codigoRegion === null) {
      return;
    }
    this.cargandoComunas.set(true);
    this.mensajeError.set(null);
    this.geoCatalogService.listarComunasDeRegion(codigoRegion).subscribe({
      next: (comunas) => {
        this.comunas.set(comunas);
        this.cargandoComunas.set(false);
      },
      error: (error: ErrorAplicacion) => {
        this.mensajeError.set(error.message);
        this.cargandoComunas.set(false);
      }
    });
  }

  protected verificarDiaHabil(): void {
    const fecha = this.fechaAVerificar.value;
    if (!fecha) {
      return;
    }
    this.verificandoDiaHabil.set(true);
    this.mensajeError.set(null);
    const fechaIso = this.formatearFechaIso(fecha);
    this.geoCatalogService.verificarDiaHabil(fechaIso).subscribe({
      next: (resultado) => {
        this.resultadoDiaHabil.set(resultado);
        this.verificandoDiaHabil.set(false);
      },
      error: (error: ErrorAplicacion) => {
        this.mensajeError.set(error.message);
        this.verificandoDiaHabil.set(false);
      }
    });
  }

  private formatearFechaIso(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }
}
