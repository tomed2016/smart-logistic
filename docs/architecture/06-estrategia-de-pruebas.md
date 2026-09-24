# ADR 06: Estrategia de pruebas

## Estado

Aceptada e implementada.

## Contexto

La plataforma está compuesta por múltiples microservicios (`customer-service`,
`geo-catalog-service`, y los que se vayan agregando) que se comunican entre sí de
forma síncrona (HTTP) y asíncrona (RabbitMQ), y que dependen de infraestructura
externa (PostgreSQL, RabbitMQ). Se necesita una estrategia de pruebas explícita
que:

- Dé retroalimentación rápida durante el desarrollo diario (sin Docker).
- Verifique la integración real de cada servicio con su base de datos y sus
  adaptadores externos.
- Verifique que el stack completo, ya empaquetado en imágenes Docker, arranca y
  se comunica correctamente de extremo a extremo.
- Verifique el comportamiento de negocio observable desde fuera del sistema, en
  lenguaje cercano al negocio (Gherkin), como lo haría un consumidor externo de
  las APIs.
- No obligue a que comandos de uso frecuente como `mvn test` en la raíz del
  proyecto dependan de Docker.

## Decisión

Se adopta una pirámide de pruebas de **cuatro niveles**, cada uno con su propio
mecanismo de invocación y su propio costo/velocidad, de modo que cada nivel se
ejecuta únicamente cuando aporta señal que los niveles inferiores no pueden dar:

### Nivel 1 — Pruebas unitarias (`mvn test`)

- Ubicación: `src/test/java` de cada servicio y de `shared-kernel`, sufijo `*Test`.
- Ejecutadas por `maven-surefire-plugin` en la fase `test`, que corre por defecto
  en cualquier `mvn test`, `mvn package`, `mvn install` o `mvn verify`.
- No requieren Docker ni red. Cubren lógica de dominio (value objects, agregados,
  políticas), mapeos y casos borde de los adaptadores en aislamiento (mocks).
- Deben ejecutarse en segundos y ser la primera línea de defensa en el ciclo de
  desarrollo local y en cada push.

### Nivel 2 — Pruebas de integración por servicio (`mvn verify`, Testcontainers)

- Ubicación: `src/test/java` de cada servicio, sufijo `*IT`.
- Ejecutadas por `maven-failsafe-plugin`, ligado a las fases `integration-test` /
  `verify` (no corren con `mvn test`, sí con `mvn verify`).
- Usan Testcontainers para levantar una instancia real de PostgreSQL (y, cuando
  corresponde, RabbitMQ) efímera y aislada por ejecución. Verifican que los
  adaptadores de persistencia, las migraciones Flyway y la configuración de
  Spring Boot funcionan contra una base de datos real, no contra un mock.
- Requieren Docker, pero no requieren que el resto de la plataforma esté
  desplegado: cada servicio se prueba de forma independiente.

### Nivel 3 — Smoke test de Docker Compose (extremo a extremo, infraestructura)

- Ubicación: `infra/docker-compose-smoke-test.ps1`.
- Levanta el stack completo (`infra/docker-compose.yml`) a partir de las
  imágenes Docker productivas de cada servicio, espera los healthchecks, y
  ejecuta un flujo mínimo (consultar una comuna, crear un cliente con esa
  comuna) contra los contenedores reales. Al finalizar, destruye contenedores y
  volúmenes del stack de prueba.
- Verifica lo que las pruebas de los niveles 1 y 2 no pueden verificar: que las
  imágenes Docker están bien construidas, que la configuración de red y
  variables de entorno entre contenedores es correcta, y que los servicios se
  encuentran entre sí como lo harán en un despliegue real.
- Usa puertos de host aislados (`15432/15433/15672/25672/18081/18082`) para
  poder convivir con una instancia de desarrollo en los puertos por defecto.

### Nivel 4 — Pruebas de aceptación BDD (Cucumber, módulo `qa-automation`)

- Ubicación: módulo Maven independiente `qa-automation` (no forma parte del
  reactor raíz; ver justificación abajo).
- Escenarios escritos en Gherkin en español (`src/test/resources/features/`),
  ejecutados contra las APIs REST reales del stack (vía `RestClient`), sin
  mocks ni Testcontainers propios: reutiliza el mismo stack que el nivel 3.
- Describe el comportamiento de negocio en el lenguaje del dominio (clientes,
  direcciones, catálogo geográfico, condiciones de pago), sirviendo además como
  documentación viva y ejecutable de las reglas de negocio implementadas.
- Se ejecuta con `mvn -f qa-automation/pom.xml verify` (o `cd qa-automation;
  mvn verify`), apuntando a las URLs base del stack ya desplegado mediante
  `-Dcustomer.service.base-url` / `-Dgeo.catalog.service.base-url` (o las
  variables de entorno equivalentes).
- No incluye Selenium: la plataforma todavía no expone una interfaz web (ver
  sección siguiente). El diseño del módulo permite añadir step definitions
  basados en Selenium/WebDriver en el futuro sin reestructurar lo existente.

## `qa-automation` como módulo Maven independiente

`qa-automation` **no** está declarado en `<modules>` del `pom.xml` raíz, aunque
hereda de él vía `<parent>`. Si estuviera en el reactor, `mvn install` o
`mvn test` ejecutados desde la raíz arrastrarían una dependencia implícita de
Docker (el stack debe estar corriendo para que sus pruebas pasen), rompiendo el
invariante de que los comandos Maven de uso frecuente en la raíz son rápidos y
no requieren infraestructura externa. Se invoca siempre de forma explícita y
aislada: `mvn -f qa-automation/pom.xml verify`.

Dentro del propio módulo, surefire está deshabilitado y solo `mvn verify`
(fase `integration-test`, vía failsafe) ejecuta el runner de Cucumber
(`RunCucumberIT`), replicando la misma convención de nomenclatura (`*IT`) que
las pruebas de integración de nivel 2.

## Por qué no se usa Selenium todavía

Selenium automatiza interacción con un navegador contra una interfaz web. La
plataforma, en su estado actual, solo expone APIs REST (`customer-service`,
`geo-catalog-service`); no existe una aplicación web que ejercitar. Incorporar
Selenium en este punto no verificaría ningún comportamiento real y constituiría
código de relleno sin valor de prueba. Esta decisión fue evaluada y confirmada
explícitamente con el responsable del producto durante la construcción del
módulo `qa-automation`.

Cuando exista un frontend web, se incorporará un nuevo paquete de step
definitions basado en Selenium/WebDriver dentro del mismo módulo
`qa-automation`, reutilizando el runner, el contexto de escenario
(`EscenarioContexto`) y las convenciones de ejecución ya establecidas.

## Consecuencias

- Cada nivel de la pirámide tiene un comando de invocación distinto y
  explícito; no hay ambigüedad sobre qué se ejecuta con `mvn test` versus
  `mvn verify` versus el smoke test versus `qa-automation`.
- El ciclo de desarrollo local (`mvn test`) permanece rápido y libre de Docker
  en todos los módulos del reactor raíz.
- Los niveles 2, 3 y 4 comparten en última instancia la misma infraestructura
  real (PostgreSQL, RabbitMQ vía Testcontainers o Docker Compose), evitando que
  una prueba pase en un entorno simulado y falle en producción por diferencias
  de configuración.
- La cobertura de negocio queda documentada en Gherkin, legible por perfiles no
  técnicos (operaciones, negocio), y ejecutable de forma automática.
- Añadir Selenium en el futuro es una extensión aditiva de `qa-automation`, no
  una reestructuración.
