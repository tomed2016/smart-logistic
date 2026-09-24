# ADR 05: Integración entre Clientes y Catálogo Geográfico

## Estado

Aceptada e implementada.

## Contexto

`customer-service` necesita validar la comuna de las direcciones de sus clientes.
El catálogo geográfico oficial vive en `geo-catalog-service`, que es la fuente de
verdad para regiones, provincias y comunas. Mantener una segunda lista completa en
Clientes produciría duplicación de datos y riesgo de divergencia.

La integración debe distinguir una comuna inexistente de una indisponibilidad
transitoria del servicio. No debe convertir una falla de infraestructura en un
error de validación de dirección.

## Decisión

Se adopta una llamada HTTP síncrona mediante `RestClient`, encapsulada detrás de
`ComunaCatalogPort` mediante `GeoCatalogHttpComunaCatalogAdapter`.

La configuración utiliza:

- `geo-catalog-service.base-url`, `connect-timeout` de 2 segundos y
  `read-timeout` de 3 segundos.
- Retry de Resilience4j con tres intentos y espera de 200 ms, limitado a errores
  transitorios de red y respuestas HTTP 5xx.
- Circuit breaker con ventana basada en conteo de 10 llamadas, mínimo de 5 llamadas,
  umbral de fallo del 50 %, espera de 30 segundos en estado abierto y 3 llamadas
  permitidas en half-open.
- Caché Caffeine local con máximo de 500 entradas y expiración de 6 horas.

La caché se consulta antes de ejecutar la llamada remota. Solo se almacenan
respuestas exitosas; los resultados vacíos no se cachean. Como el catálogo cambia
mediante migraciones Flyway y no durante la operación normal, la expiración de 6
horas reduce latencia y dependencia del servicio sin introducir una obsolescencia
significativa.

## Semántica de errores

- HTTP 404 y códigos no numéricos producen `Optional.empty()`: la comuna no existe
  en el catálogo oficial.
- Timeout, conexión rechazada, errores 5xx, reintentos agotados o circuito abierto
  producen `ComunaCatalogNoDisponibleException`, que se expone como HTTP 503.

Esta separación evita rechazar una dirección válida por una caída temporal del
catálogo y permite al consumidor aplicar una política de reintento o recuperación
adecuada.

## Traducción de regiones y códigos

El servicio remoto usa códigos INE. El modelo de Clientes conserva `Comuna.codigo`
como `String` para no requerir una migración de esquema, pero en el adaptador HTTP
el valor debe contener el código INE numérico. `Region.porCodigoIne` realiza una
traducción explícita al enum local; no se utiliza el ordinal del enum, porque el
orden de declaración local no coincide con la codificación INE.

El `InMemoryComunaCatalogAdapter` mantiene sus códigos simplificados para desarrollo
offline y se activa solo con el perfil `local`. Los datos creados bajo ese perfil
no deben confundirse con códigos INE de un entorno integrado.

## Alternativas consideradas

1. **Copia asíncrona del catálogo:** descartada por agregar sincronización,
   versionado y una segunda fuente persistida para un conjunto pequeño y estable de
   datos.
2. **Consulta HTTP sin resiliencia ni caché:** descartada porque una dependencia de
   referencia no debe convertir una caída breve en una cascada de fallas.
3. **Compartir el catálogo como librería:** descartada porque cada servicio tendría
   que redeplegarse para actualizar datos de referencia y se perdería una fuente de
   verdad única.

## Consecuencias

Clientes queda desacoplado de la implementación concreta mediante su puerto de
salida y obtiene el catálogo completo sin duplicar persistencia. La solución añade
una dependencia de disponibilidad entre servicios, mitigada por caché, timeouts,
retry y circuit breaker. La selección del adaptador está controlada por perfiles:
el HTTP es el comportamiento predeterminado y `local` es una excepción explícita
para desarrollo offline.

La prueba unitaria del adaptador cubre mapeo, 404, códigos inválidos, 5xx y
fallback. La aplicación efectiva de las anotaciones de Resilience4j depende del
proxy de Spring y debe validarse en un entorno de aplicación levantado, no mediante
una instancia Java directa del adaptador.
