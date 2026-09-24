import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'clientes' },
  {
    path: 'clientes',
    loadChildren: () => import('./features/clientes/clientes.routes').then((m) => m.CLIENTES_ROUTES)
  },
  {
    path: 'catalogo-geografico',
    loadComponent: () =>
      import('./features/catalogo-geografico/pages/catalogo-geografico/catalogo-geografico.page').then(
        (m) => m.CatalogoGeograficoPage
      )
  },
  { path: '**', redirectTo: 'clientes' }
];
