# Smart Logistic — Visión Arquitectónica

## 1. Contexto
Plataforma de gestión logística y administrativa para una empresa chilena de venta y
distribución de bidones de agua. Debe operar en producción, escalar progresivamente,
soportar web (inicialmente) y apps móviles (futuro), desplegarse en contenedores Docker
sobre una nube aún no definida (portabilidad = requisito no funcional).

## 2. Decisión de estilo arquitectónico
Ver `02-adr-microservices.md`. Resumen: **microservicios desde el día 1**, uno por
bounded context, con arquitectura **hexagonal (ports & adapters)** interna en cada
servicio, comunicación síncrona (REST/OpenAPI) para consultas y comunicación asíncrona
(RabbitMQ, patrón *event-driven* + *transactional outbox*) para integración entre
contextos.

## 3. Bounded contexts identificados (mapa de contexto preliminar)

| Bounded Context              | Responsabilidad                                                                 | Tipo de relación                          |
|-------------------------------|----------------------------------------------------------------------------------|--------------------------------------------|
| **Clientes**                  | Identidad de clientes, direcciones, coordenadas, condiciones comerciales         | Upstream (Core Domain de soporte) — *Open Host Service* |
| **Catálogo Geográfico (CL)**  | Regiones, provincias, comunas, feriados, calendario hábil                        | *Shared Kernel* publicado como librería + servicio de referencia |
| **Productos e Inventario**    | Productos, stock, bidones llenos/vacíos/retornables/dañados, movimientos        | Upstream de Pedidos                        |
| **Pedidos**                    | Ciclo de vida del pedido, recurrencias, reprogramación                          | Core Domain — *Customer/Supplier* de Inventario y Clientes |
| **Planificación Logística**    | Ruteo, asignación de vehículos, ventanas horarias                               | Downstream de Pedidos — *Conformist* de Clientes (direcciones) |
| **Pagos y Cobranza**          | Pagos, deudas, cuentas por cobrar                                                | Downstream de Pedidos                      |
| **Identidad y Acceso**        | Autenticación, autorización, usuarios operativos                                | *Shared Kernel* técnico (no de negocio)    |

Cada contexto se implementa como microservicio Spring Boot independiente con su propia
base de datos (**Database per Service**), evitando joins entre contextos. La
integración entre contextos se realiza mediante eventos de dominio (RabbitMQ) y APIs
REST versionadas (`/api/v1/...`).

## 4. Identificadores
- **Regla dura del negocio**: el RUT **nunca** es identificador técnico interno. Cada
  agregado usa un `UUID` (`ClienteId`, `PedidoId`, etc.) generado en el momento de
  creación. El RUT se modela como *Value Object* (`Rut`) con su propio dígito
  verificador, único a nivel de negocio (constraint `UNIQUE` en BD), pero nunca como PK
  ni como clave foránea entre servicios.

## 5. Orden de implementación (roadmap iterativo)
1. **Iteración 1 (completa)**: `shared-kernel` (VOs comunes) + microservicio **Clientes**.
2. **Iteración 2 (completa)**: Catálogo Geográfico CL (regiones/provincias/comunas/
   feriados) como servicio de referencia (`geo-catalog-service`) + librería de
   calendario hábil `America/Santiago` (`CalendarioChileno` en `shared-kernel`). Ver
   `04-bounded-context-catalogo-geografico.md` para el detalle y las decisiones
   diferidas (regla Ley 20.983).
3. **Iteración 3 (siguiente)**: Productos e Inventario.
4. Iteración 4: Pedidos (estados, auditoría, historial inmutable).
5. Iteración 5: Recurrencias (contratos de suministro).
6. Iteración 6: Reprogramación (pedidos, entregas, ocurrencias).
7. Iteración 7: Planificación logística / ruteo.
8. Iteración 8: Pagos y cobranza.
9. Iteración 9: Observabilidad transversal (OpenTelemetry, dashboards), resiliencia
   (circuit breakers, retries, outbox), seguridad (OAuth2/OIDC), CI/CD, IaC multi-nube.

Cada iteración entrega: diseño documentado (ADR si aplica), código que compila,
pruebas automatizadas, y Docker Compose funcional para validar localmente.
