# smart-logistic

## Smoke test Docker Compose

Con Docker Desktop en ejecucion, el smoke test levanta el stack completo, espera
los healthchecks, consulta la comuna INE `13119` desde `geo-catalog-service` y
crea un cliente con una direccion en `customer-service`. Al finalizar elimina
contenedores y volumenes del proyecto de prueba:

```powershell
.\infra\docker-compose-smoke-test.ps1
```

Para reutilizar imagenes ya construidas:

```powershell
.\infra\docker-compose-smoke-test.ps1 -SkipBuild
```

> **Nota (Windows):** si la politica de ejecucion de PowerShell del equipo
> bloquea scripts locales sin firmar (`... no se puede cargar porque la
> ejecucion de scripts esta deshabilitada ...`), ejecuta el script con:
> ```powershell
> powershell -NoProfile -ExecutionPolicy Bypass -File .\infra\docker-compose-smoke-test.ps1
> ```
> Esto aplica la politica solo al proceso del script, sin alterar la
> configuracion permanente del sistema.

El stack usa puertos de host aislados (15432/15433/15672/25672/18081/18082)
para no chocar con una instancia de desarrollo ya corriendo en los puertos por
defecto (5432/5433/5672/15672/8081/8082); el script restaura las variables de
entorno originales al terminar, incluso si una aseveracion falla.