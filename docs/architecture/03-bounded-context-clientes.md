# Bounded Context: Clientes

## 1. Responsabilidad
Gestionar la identidad comercial de los clientes: datos de contacto, direcciones con
coordenadas geográficas, condiciones de pago y prioridad comercial. Es el *Open Host
Service* consultado por Pedidos y Planificación Logística.

## 2. Lenguaje ubicuo
- **Cliente**: persona natural o empresa que compra bidones de agua.
- **Rut**: identificador legal chileno del cliente (Value Object, validado con dígito
  verificador). *Nunca* es clave técnica.
- **ClienteId**: identificador técnico interno (UUID), único.
- **Dirección**: lugar de entrega asociado a un cliente; incluye comuna y coordenadas
  (lat/lng) para planificación de rutas.
- **Comuna / Provincia / Región**: catálogo geográfico chileno (en esta iteración se
  modela como VO + tabla de referencia local; en la Iteración 2 se extrae a un
  servicio de Catálogo Geográfico compartido).
- **Condición de pago**: `CONTADO`, `CREDITO_7_DIAS`, `CREDITO_15_DIAS`,
  `CREDITO_30_DIAS`.
- **Prioridad comercial**: `ESTANDAR`, `PREFERENTE`, `VIP`.

## 3. Agregado raíz: `Cliente`
Invariantes:
- Un `Cliente` tiene un único `Rut` válido y único en el sistema.
- Un `Cliente` debe tener al menos un medio de contacto (teléfono o correo) para ser
  `ACTIVO`.
- Debe existir **exactamente una** dirección marcada como principal (`esPrincipal`)
  cuando el cliente tiene al menos una dirección.
- Un `Cliente` no se elimina físicamente: se **desactiva** (`estado = INACTIVO`),
  preservando el historial comercial (regla transversal de auditoría del sistema).
- Toda mutación relevante (creación, edición, cambio de estado, alta/baja de
  dirección) publica un evento de dominio (`ClienteCreado`, `ClienteActualizado`,
  `DireccionAgregada`, `ClienteDesactivado`) vía *Transactional Outbox*.

## 4. Casos de uso (Iteración 1)
| Caso de uso                    | Descripción                                                        |
|---------------------------------|---------------------------------------------------------------------|
| `CrearClienteUseCase`           | Registra un cliente nuevo con RUT único y datos de contacto.        |
| `ActualizarClienteUseCase`      | Edita datos de contacto, condición de pago, prioridad comercial.    |
| `AgregarDireccionUseCase`       | Asocia una nueva dirección (con coordenadas) a un cliente existente.|
| `DesactivarClienteUseCase`      | Marca un cliente como inactivo (no elimina historial).              |
| `ConsultarClienteUseCase`       | Obtiene un cliente por `ClienteId`, o lista paginada con filtros.   |

## 5. API REST (v1)
```
POST   /api/v1/clientes                         Crear cliente
GET    /api/v1/clientes/{clienteId}              Obtener cliente
GET    /api/v1/clientes?comuna=&estado=&page=    Listar clientes (paginado, filtros)
PUT    /api/v1/clientes/{clienteId}              Actualizar datos de cliente
POST   /api/v1/clientes/{clienteId}/direcciones  Agregar dirección
PUT    /api/v1/clientes/{clienteId}/direcciones/{direccionId}   Actualizar dirección
DELETE /api/v1/clientes/{clienteId}              Desactivar cliente (soft-delete lógico)
```

## 6. Eventos de dominio publicados
- `cliente.creado.v1`
- `cliente.actualizado.v1`
- `cliente.direccion-agregada.v1`
- `cliente.desactivado.v1`

## 7. Persistencia
- PostgreSQL, esquema `clientes`, migraciones Flyway (`V1__init_schema.sql`,
  `V2__seed_catalogo_geografico.sql`).
- Tabla `outbox_event` para el patrón Transactional Outbox.

## 8. Fuera de alcance en esta iteración
- Historial de pagos y entregas (pertenece a Pedidos/Pagos; Clientes solo expone su
  identidad y expondrá luego una vista de solo lectura vía composición de eventos).
- Autenticación/autorización real (se documenta el punto de extensión con Spring
  Security + JWT, pero no se implementa hasta la iteración de Identidad y Acceso).
