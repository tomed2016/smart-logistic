import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { AgregarDireccionRequest } from '../../models/cliente.model';
import { Comuna } from '../../../catalogo-geografico/models/geo.model';
import { GeoCatalogService } from '../../../catalogo-geografico/services/geo-catalog.service';

/**
 * Diálogo modal para agregar una dirección a un cliente. La comuna se busca por
 * nombre contra `geo-catalog-service` (autocompletado) y se resuelve a su código
 * numérico, que se envía como `string` según el contrato de `AgregarDireccionRequest`.
 */
@Component({
  selector: 'app-direccion-form-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './direccion-form-dialog.html',
  styleUrl: './direccion-form-dialog.scss'
})
export class DireccionFormDialog {
  private readonly formBuilder = inject(FormBuilder);
  private readonly geoCatalogService = inject(GeoCatalogService);
  private readonly dialogRef = inject(MatDialogRef<DireccionFormDialog>);

  protected readonly comunasSugeridas = signal<Comuna[]>([]);
  protected readonly comunaSeleccionada = signal<Comuna | null>(null);

  protected readonly formulario = this.formBuilder.nonNullable.group({
    calle: ['', Validators.required],
    numero: ['', Validators.required],
    comunaTexto: ['', Validators.required],
    latitud: [-33.4489, [Validators.required, Validators.min(-90), Validators.max(90)]],
    longitud: [-70.6693, [Validators.required, Validators.min(-180), Validators.max(180)]],
    referencia: [''],
    marcarComoPrincipal: [false]
  });

  constructor() {
    this.formulario.controls.comunaTexto.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((texto) => {
          this.comunaSeleccionada.set(null);
          if (!texto || texto.trim().length < 2) {
            return [];
          }
          return this.geoCatalogService.buscarComunasPorNombre(texto.trim());
        })
      )
      .subscribe((comunas) => this.comunasSugeridas.set(comunas));
  }

  protected seleccionarComuna(comuna: Comuna): void {
    this.comunaSeleccionada.set(comuna);
    this.formulario.controls.comunaTexto.setValue(comuna.nombre, { emitEvent: false });
  }

  protected cancelar(): void {
    this.dialogRef.close();
  }

  protected confirmar(): void {
    const comuna = this.comunaSeleccionada();
    if (this.formulario.invalid || !comuna) {
      this.formulario.markAllAsTouched();
      return;
    }
    const valores = this.formulario.getRawValue();
    const resultado: AgregarDireccionRequest = {
      calle: valores.calle,
      numero: valores.numero,
      codigoComuna: String(comuna.codigo),
      latitud: valores.latitud,
      longitud: valores.longitud,
      referencia: valores.referencia || null,
      marcarComoPrincipal: valores.marcarComoPrincipal
    };
    this.dialogRef.close(resultado);
  }
}
