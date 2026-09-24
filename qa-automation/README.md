# qa-automation — Pruebas de aceptación (BDD) con Cucumber

Suite de pruebas de aceptación en Gherkin/Cucumber que ejercita el **comportamiento
de negocio observable** de la plataforma llamando a las APIs REST reales de
`customer-service` y `geo-catalog-service`, tal como lo haría un cliente HTTP externo.
No usa Selenium: la plataforma aún no tiene interfaz web (ver
`docs/architecture/06-estrategia-de-pruebas.md` para la justificación completa de esta
decisión y de cómo encaja esta suite en la pirámide de pruebas del proyecto).

## Por qué es un módulo Maven independiente (no un módulo del reactor raíz)

Este módulo **no** está listado en `<modules>` del `pom.xml` raíz. Es una decisión
deliberada: estas pruebas requieren que el stack completo de
`infra/docker-compose.yml` esté corriendo (bases de datos, RabbitMQ,
customer-service, geo-catalog-service). Si `qa-automation` fuera un módulo del
reactor, `mvn test` o `mvn install` ejecutados desde la raíz del proyecto
empezarían a depender implícitamente de Docker, rompiendo el invariante que se ha
mantenido en todas las iteraciones anteriores: **`mvn test` en la raíz siempre es
rápido y nunca requiere infraestructura externa**.

Sí hereda de `pom.xml` raíz vía `<parent>` (mismas versiones de Spring Boot, Java 21,
etc.), pero se invoca siempre de forma explícita:

```powershell
mvn -f qa-automation/pom.xml verify
# o, de forma equivalente:
cd qa-automation; mvn verify
```

Dentro del propio módulo, `mvn test` tampoco ejecuta nada (surefire está
deshabilitado): los escenarios Cucumber se ejecutan únicamente con `mvn verify`
(fase `integration-test`, vía `maven-failsafe-plugin`), exactamente igual que las
pruebas `*IT` basadas en Testcontainers de `customer-service` y
`geo-catalog-service`.

## Prerrequisitos

1. Docker Desktop en ejecución.
2. El stack de la plataforma levantado. Dos formas de hacerlo:

   **Opción A — puertos por defecto** (si no tienes otra cosa usando 5432/9081/9082):

   ```powershell
   cd infra
   docker compose up -d
   ```

   **Opción B — puertos aislados** (recomendado si ya tienes otro proyecto usando
   esos puertos; son los mismos puertos que usa
   `infra/docker-compose-smoke-test.ps1`):

   ```powershell
   cd infra
   $env:CUSTOMER_POSTGRES_PORT = "15432"
   $env:GEO_POSTGRES_PORT = "15433"
   $env:RABBITMQ_PORT = "15672"
   $env:RABBITMQ_MANAGEMENT_PORT = "25672"
   $env:CUSTOMER_SERVICE_PORT = "18081"
   $env:GEO_CATALOG_SERVICE_PORT = "18082"
   docker compose up -d
   ```

3. Espera a que ambos servicios respondan en `/actuator/health` con `"status":"UP"`.

## Ejecutar la suite

Con los puertos por defecto (9081/9082), no se necesita configuración adicional:

```powershell
mvn -f qa-automation/pom.xml verify
```

Con puertos aislados (Opción B), sobrescribe las URLs base:

```powershell
mvn -f qa-automation/pom.xml verify `
  "-Dcustomer.service.base-url=http://localhost:18081" `
  "-Dgeo.catalog.service.base-url=http://localhost:18082"
```

También son configurables por variable de entorno (útil en CI), gracias al binding
relajado de Spring Boot:

```powershell
$env:CUSTOMER_SERVICE_BASE_URL = "http://localhost:18081"
$env:GEO_CATALOG_SERVICE_BASE_URL = "http://localhost:18082"
mvn -f qa-automation/pom.xml verify
```

## Ver los resultados

- Consola: salida `pretty` de Cucumber con cada escenario y paso.
- Reporte HTML: `qa-automation/target/cucumber-report/index.html`.
- Reporte JSON (para integraciones CI): `qa-automation/target/cucumber-report/cucumber.json`.
- Reporte Failsafe (formato JUnit XML, agregado por escenario):
  `qa-automation/target/failsafe-reports/`.

## Estructura del módulo

```
qa-automation/
  pom.xml
  src/test/resources/
    application.yml                 URLs base por defecto (localhost:9081 / :9082)
    features/
      gestion_clientes.feature      Alta, consulta, actualización, desactivación,
                                     direcciones, RUT duplicado
      catalogo_geografico.feature   Regiones, comunas, días hábiles
  src/test/java/cl/smartlogistic/qa/
    QaAutomationTestApplication.java   Contexto Spring Boot minimo (sin servidor web)
    RunCucumberIT.java                 Punto de entrada JUnit 5 (motor Cucumber)
    config/
      CucumberSpringConfiguration.java @CucumberContextConfiguration
      CustomerServiceProperties.java   @ConfigurationProperties customer.service.*
      GeoCatalogServiceProperties.java @ConfigurationProperties geo.catalog.service.*
      RestClientsConfig.java           Beans RestClient (uno por servicio)
    support/
      EscenarioContexto.java   Estado compartido entre pasos de un mismo escenario
      GeneradorDeRut.java      RUTs de prueba válidos (reutiliza shared-kernel.Rut)
    steps/
      ClienteSteps.java
      CatalogoGeograficoSteps.java
      RespuestaHttpSteps.java  Aserción genérica de código de estado HTTP
```

## Por qué no se usa Selenium (por ahora)

La plataforma expone hoy únicamente APIs REST (`customer-service`,
`geo-catalog-service`); no existe todavía una interfaz web que ejercitar con un
navegador. Introducir Selenium en este punto no probaría ningún comportamiento
real y se convertiría en código de relleno. Cuando exista un frontend web, se
puede añadir sin reestructurar este módulo: un nuevo paquete
`cl.smartlogistic.qa.web` con sus propios step definitions y un
`WebDriver`/`RemoteWebDriver` gestionado en un bean adicional de
`config/`, reutilizando el mismo runner (`RunCucumberIT`), el mismo
`EscenarioContexto` y las mismas convenciones de ejecución (`mvn verify`).

## Escenarios cubiertos

**Gestión de clientes** (`gestion_clientes.feature`):

- Crear un cliente persona natural con datos válidos.
- Consultar un cliente existente por su identificador.
- Agregar una dirección con una comuna válida (integración cruzada con
  `geo-catalog-service`).
- Rechazar una dirección cuya comuna no existe en el catálogo (400).
- Actualizar el nombre comercial de un cliente.
- Desactivar un cliente y verificar que su historial se preserva (204 + estado
  `INACTIVO`, nunca se elimina el registro).
- Rechazar la creación de un cliente con un RUT ya registrado (409).
- Consultar un cliente inexistente (404).

**Catálogo geográfico** (`catalogo_geografico.feature`):

- Consultar una región existente.
- Listar las comunas de una región.
- Consultar una comuna existente por código.
- Consultar una comuna inexistente (404).
- Verificar que un domingo no es día hábil.

## Datos de prueba

Cada escenario que crea un cliente genera un RUT chileno **válido** (dígito
verificador correcto, módulo 11) y aleatorio en tiempo de ejecución mediante
`GeneradorDeRut`, que reutiliza el Value Object `Rut` del `shared-kernel` — la
única fuente de verdad del algoritmo en toda la plataforma — en vez de
reimplementarlo o hardcodear RUTs fijos que colisionarían entre ejecuciones
sucesivas contra el mismo stack (el RUT es único de negocio en `customer-service`).

La comuna `13119` (Maipú, Región Metropolitana) se reutiliza como comuna válida
conocida, igual que en el smoke test de Docker Compose.
