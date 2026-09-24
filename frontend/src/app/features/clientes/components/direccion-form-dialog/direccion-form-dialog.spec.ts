import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { environment } from '../../../../../environments/environment';
import { Comuna } from '../../../catalogo-geografico/models/geo.model';
import { DireccionFormDialog } from './direccion-form-dialog';

describe('DireccionFormDialog', () => {
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.geoCatalogServiceBaseUrl}/api/v1/comunas`;
  const comunaDeEjemplo: Comuna = { codigo: 13119, nombre: 'Providencia', provinciaCodigo: 131, regionCodigo: 13 };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DireccionFormDialog],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNoopAnimations(),
        { provide: MatDialogRef, useValue: { close: vi.fn() } }
      ]
    }).compileComponents();
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('busca comunas por nombre al escribir en el campo de comuna', async () => {
    const fixture = TestBed.createComponent(DireccionFormDialog);
    const componente = fixture.componentInstance as any;
    fixture.detectChanges();

    componente.formulario.controls.comunaTexto.setValue('Provid');
    await new Promise((resolve) => setTimeout(resolve, 350));

    const request = httpMock.expectOne((req) => req.url === baseUrl);
    request.flush([comunaDeEjemplo]);

    expect(componente.comunasSugeridas()).toEqual([comunaDeEjemplo]);
  });

  it('cierra el diálogo con la solicitud construida al confirmar con una comuna seleccionada', () => {
    const fixture = TestBed.createComponent(DireccionFormDialog);
    const componente = fixture.componentInstance as any;
    const dialogRef = TestBed.inject(MatDialogRef) as any;
    fixture.detectChanges();

    componente.formulario.controls.calle.setValue('Av. Siempre Viva');
    componente.formulario.controls.numero.setValue('123');
    componente.seleccionarComuna(comunaDeEjemplo);

    componente.confirmar();

    expect(dialogRef.close).toHaveBeenCalledWith(
      expect.objectContaining({ calle: 'Av. Siempre Viva', numero: '123', codigoComuna: '13119' })
    );
  });

  it('no cierra el diálogo si no se ha seleccionado una comuna válida', () => {
    const fixture = TestBed.createComponent(DireccionFormDialog);
    const componente = fixture.componentInstance as any;
    const dialogRef = TestBed.inject(MatDialogRef) as any;
    fixture.detectChanges();

    componente.formulario.controls.calle.setValue('Av. Siempre Viva');
    componente.formulario.controls.numero.setValue('123');

    componente.confirmar();

    expect(dialogRef.close).not.toHaveBeenCalled();
  });
});
