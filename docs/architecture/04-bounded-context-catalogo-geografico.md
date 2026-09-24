# Bounded Context: Catálogo Geográfico CL

## 1. Responsabilidad
Ser la fuente de verdad de la división político-administrativa de Chile (regiones,
provincias, comunas) y del calendario de días hábiles chileno (fines de semana +
feriados legales). Es un *Open Host Service* de solo lectura consumido por Clientes
(direcciones), Pedidos (validación de comuna de entrega) y Planificación Logística
(zonificación de rutas y cálculo del "siguiente día hábil").

Este bounded context se materializa en **dos artefactos complementarios**, no uno solo:

| Artefacto | Qué resuelve | Por qué |
|---|---|---|
| `shared-kernel` → `cl.smartlogistic.shared.calendar` | Cálculo de feriados y días hábiles | Es lógica de dominio pura (sin estado, sin infraestructura) que **todos** los servicios necesitan invocar en proceso (p. ej. Pedidos al confirmar una fecha de entrega, Planificación al generar rutas). Exponerla solo vía HTTP forzaría una llamada de red por cada validación de fecha, lo cual es innecesario para una función determinista y sin efectos secundarios. |
| `geo-catalog-service` | Catálogo de regiones/provincias/comunas | Es **dato de referencia** (346 comunas, códigos INE oficiales) que debe vivir en un solo lugar y ser consultado vía API — no tiene sentido embeberlo como librería porque cambiaría la fuente de verdad en cada servicio que la use, y su tamaño (346 filas) no justifica duplicarlo en cada base de datos de cada microservicio. |

Esta dualidad (librería para *comportamiento* determinista, servicio para *datos* de
referencia compartidos) es una decisión arquitectónica explícita de esta iteración.

## 2. Lenguaje ubicuo
- **Región**: división administrativa de primer nivel (16 en Chile, código INE de 1-2
  dígitos, ej. `13` = Metropolitana de Santiago).
- **Provincia**: división administrativa de segundo nivel, pertenece a una única
  región (56 en Chile, código INE de 3 dígitos, ej. `131` = Santiago).
- **Comuna**: división administrativa de tercer nivel, pertenece a una única provincia
  (346 en Chile, código INE de 4-5 dígitos, ej. `13119` = Maipú).
- **Feriado**: fecha no laborable según la ley chilena. Se clasifica en `FIJO`
  (fecha calendario constante), `MOVIL_PASCUA` (calculado desde el Domingo de Pascua),
  `MOVIL_LEY_19668` (feriados que se trasladan al lunes más cercano según la Ley
  19.668) y `AD_HOC` (feriados legislados puntualmente, ej. "días sándwich").
- **Día hábil**: día que no es sábado, domingo, ni feriado.

## 3. Modelo de dominio
`Region`, `Provincia` y `Comuna` son **records inmutables identificados por su código
INE oficial** (no UUID). Es la única excepción documentada al patrón "nunca usar
claves de negocio como identificador técnico" (usado, por ejemplo, para el RUT en
Clientes): a diferencia de un RUT o un pedido, el código INE de una comuna es:
- Emitido y mantenido por una autoridad externa estable (INE / SUBDERE), no por este
  sistema.
- Prácticamente inmutable (el último cambio estructural fue la creación de la región
  de Ñuble en 2018-2019).
- Ya utilizado como estándar de facto en integraciones estatales y de transporte en
  Chile, por lo que preservarlo como identificador reduce fricción de integración.

`CalendarioChileno` (shared-kernel) no persiste nada: calcula feriados algorítmicamente
en cada invocación. No hay un agregado de dominio típico aquí — es un *domain service*
puro sin estado.

## 4. Estrategia de cálculo de feriados y su alcance documentado
Implementado en `CalendarioChileno`:
- **11 feriados fijos**: Año Nuevo, Día del Trabajo, Glorias Navales, Virgen del
  Carmen, Asunción de la Virgen, Independencia Nacional, Glorias del Ejército,
  Iglesias Evangélicas y Protestantes, Todos los Santos, Inmaculada Concepción,
  Navidad.
- **2 feriados móviles de Pascua**: Viernes Santo y Sábado Santo, calculados con el
  algoritmo de Meeus/Jones/Butcher (independiente de tablas pre-calculadas, válido
  para cualquier año del calendario gregoriano).
- **2 feriados con traslado por Ley 19.668**: San Pedro y San Pablo (29-jun) y
  Encuentro de Dos Mundos (12-oct). Regla de traslado: si cae martes, miércoles o
  jueves se traslada al lunes anterior; si cae viernes se traslada al lunes
  siguiente; si cae lunes, sábado o domingo no se traslada.
- **Punto de extensión `AD_HOC`**: constructor que acepta un `Set<LocalDate>`
  adicional, para feriados legislados puntualmente año a año (ej. "días sándwich" por
  ley específica) que **no pueden inferirse algorítmicamente** y deben cargarse desde
  una fuente verificada.

**Gap conocido y explícitamente NO implementado en esta iteración**: la Ley 20.983
que agrega un feriado adicional en septiembre cuando el 18 de septiembre cae martes
(se suma el 17) o miércoles (se suma el 20), para generar un fin de semana largo de
Fiestas Patrias. No se implementó porque depende de una regla condicional sobre el
calendario de cada año que conviene verificar puntualmente contra el Diario Oficial
antes de automatizar; se deja como extensión futura vía el mecanismo `AD_HOC` ya
disponible. **Se declara explícitamente para que no se asuma cobertura silenciosa.**

## 5. Fuente de datos del catálogo geográfico y su procedencia
Los 16/56/346 registros de regiones/provincias/comunas fueron obtenidos del repositorio
público `knxroot/bdcut-cl` (GitHub, licencia GPLv3, 173 stars), archivo
`BD/CSV_utf8/BDCUT_CL__CSV_UTF8.csv`, que reconstruye el "Base de Datos Único de
Códigos Único Territorial" con códigos INE oficiales. Se verificó que el conteo de
346 comunas coincide con la división político-administrativa vigente
(post-creación de la Región de Ñuble, 2018).

Como en la Iteración 1 (con el catálogo de comunas simplificado de clientes), se
documenta explícitamente esta procedencia en vez de fabricar los datos desde memoria,
siguiendo el principio de transparencia sobre exactitud establecido en ese momento.
Los datos crudos (`docs/data/bdcut-cl-raw.csv`) y el script de generación de seeds
(`docs/data/gen_seed.ps1`) se conservan en el repositorio como registro de
procedencia/reproducibilidad. Nota de licencia: los *datos* (códigos y nombres
oficiales de división administrativa) son hechos de dominio público (no protegidos
por copyright); se atribuye la fuente por transparencia y trazabilidad, no por
obligación de la GPL sobre el código de este proyecto (el CSV no se distribuye como
parte del artefacto compilado, solo como insumo de las migraciones Flyway).

## 6. Casos de uso (Iteración 2)
| Caso de uso                            | Descripción                                               |
|-----------------------------------------|-------------------------------------------------------------|
| `ConsultarCatalogoGeograficoUseCase`     | Lista/busca regiones, provincias y comunas.                 |
| `ConsultarFeriadosUseCase`               | Lista feriados de un año, verifica si una fecha es día hábil, calcula el siguiente día hábil. |

## 7. API REST (v1)
```
GET /api/v1/regiones                       Listar las 16 regiones
GET /api/v1/regiones/{codigo}              Obtener una región
GET /api/v1/regiones/{codigo}/provincias   Listar provincias de una región
GET /api/v1/regiones/{codigo}/comunas      Listar comunas de una región
GET /api/v1/provincias/{codigo}/comunas    Listar comunas de una provincia
GET /api/v1/comunas?nombre=                Listar/buscar comunas por nombre
GET /api/v1/comunas/{codigo}               Obtener una comuna
GET /api/v1/feriados?anio=                 Listar feriados de un año
GET /api/v1/dias-habiles/verificar?fecha=  Verificar si una fecha es día hábil
GET /api/v1/dias-habiles/siguiente?desde=  Calcular el siguiente día hábil desde una fecha
```

## 8. Persistencia
- PostgreSQL, esquema propio (`geo_catalog_service`), migraciones Flyway:
  `V1__init_schema.sql` (esquema con FKs región→provincia→comuna),
  `V2__seed_regiones.sql`, `V3__seed_provincias.sql`, `V4__seed_comunas.sql` (346
  filas, generadas desde `docs/data/bdcut-cl-raw.csv`).
- **Los feriados NO se persisten**: se calculan en cada consulta vía `CalendarioChileno`
  (bean expuesto por `CalendarioConfig`). Persistirlos implicaría mantener dos fuentes
  de verdad sincronizadas (la tabla y el algoritmo); al ser una función pura y barata
  de calcular, no aporta valor materializarla.

## 9. Sin mensajería (RabbitMQ)
`geo-catalog-service` no publica eventos de dominio: es un servicio de referencia de
solo lectura cuyo único mecanismo de cambio es una migración Flyway (deploy), no una
acción de usuario. No hay invariantes transaccionales que proteger con eventos.

## 10. Decisión diferida: integración con `customer-service`
`customer-service` mantiene su propio `InMemoryComunaCatalogAdapter` (una lista
simplificada de comunas embebida en código, de la Iteración 1) y **no se migra** en
esta iteración a consumir `geo-catalog-service` vía HTTP. Se documenta como
*technical debt* explícito, no como omisión accidental:
- Migrarlo requiere decidir el patrón de integración (llamada síncrona con caché
  local + circuit breaker, vs. sincronización asíncrona de una copia de solo lectura)
  — decisión que amerita su propia iteración/ADR en vez de resolverse de paso aquí.
- El catálogo simplificado actual de `customer-service` sigue siendo funcionalmente
  correcto para las comunas que ya soporta; no bloquea el desarrollo de siguientes
  iteraciones (Productos, Pedidos).
- **Seguimiento**: se debe crear un ADR dedicado antes de la iteración de
  Planificación Logística (que sí necesitará el catálogo completo de 346 comunas con
  coordenadas para zonificación), momento en el que la integración deja de ser
  opcional.

## 11. Coexistencia de dos conceptos "Región" (no unificados)
`customer-service.domain.model.Region` es un **enum cerrado** (ej.
`METROPOLITANA_DE_SANTIAGO`) definido para las necesidades acotadas de ese bounded
context en la Iteración 1. `geo-catalog-service.domain.model.Region` es un **record**
identificado por código INE, que representa el catálogo oficial completo. Son
conceptos deliberadamente independientes en este momento del proyecto (Iteración 2):
unificarlos prematuramente acoplaría Clientes a un servicio externo antes de
resolver el punto 9. Se revisita junto con esa decisión diferida.

## 12. Fuera de alcance en esta iteración
- Persistencia o versionado histórico de cambios en la división político-administrativa
  (ej. si una comuna cambiara de provincia en el futuro). El catálogo se trata como
  snapshot vigente, no como serie temporal.
- Regla de feriado adicional Ley 20.983 (Fiestas Patrias), ver sección 4.
- Coordenadas geográficas por comuna (centroide/polígono) para zonificación de rutas
  — se añadirá en la iteración de Planificación Logística.
