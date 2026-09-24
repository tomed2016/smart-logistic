[CmdletBinding()]
param(
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$composeFile = Join-Path $PSScriptRoot "docker-compose.yml"
$projectName = "smart-logistic-smoke"
$composeArgs = @("--project-name", $projectName, "--file", $composeFile)
$smokePorts = @{
    CUSTOMER_POSTGRES_PORT = "15432"
    GEO_POSTGRES_PORT = "15433"
    RABBITMQ_PORT = "15672"
    RABBITMQ_MANAGEMENT_PORT = "25672"
    CUSTOMER_SERVICE_PORT = "18081"
    GEO_CATALOG_SERVICE_PORT = "18082"
}
$originalEnvironment = @{}
foreach ($name in $smokePorts.Keys) {
    $originalEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, "Process")
    [Environment]::SetEnvironmentVariable($name, $smokePorts[$name], "Process")
}
$customerBaseUrl = "http://localhost:$($smokePorts.CUSTOMER_SERVICE_PORT)"
$geoCatalogBaseUrl = "http://localhost:$($smokePorts.GEO_CATALOG_SERVICE_PORT)"

function Invoke-Compose {
    param([string[]]$Arguments)

    & docker compose @composeArgs @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose fallo con codigo $LASTEXITCODE."
    }
}

function Invoke-JsonRequest {
    param(
        [string]$Uri,
        [string]$Method = "Get",
        [object]$Body
    )

    $request = @{
        Uri = $Uri
        Method = $Method
        ContentType = "application/json"
        UseBasicParsing = $true
    }
    if ($null -ne $Body) {
        $request.Body = $Body | ConvertTo-Json -Depth 5
    }

    try {
        return Invoke-RestMethod @request
    } catch {
        throw "Solicitud $Method $Uri fallo: $($_.Exception.Message)"
    }
}

function Wait-ForHealth {
    param(
        [string]$ServiceName,
        [string]$Uri,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            $health = Invoke-JsonRequest -Uri $Uri
            if ($health.status -eq "UP") {
                Write-Host "$ServiceName esta saludable."
                return
            }
        } catch {
            # El servicio puede estar iniciando; el timeout reporta el error final.
        }
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)

    throw "Timeout esperando health de $ServiceName ($Uri)."
}

try {
    if (-not $SkipBuild) {
        Invoke-Compose -Arguments @("build")
    }
    Invoke-Compose -Arguments @("up", "-d")

    Wait-ForHealth -ServiceName "geo-catalog-service" -Uri "$geoCatalogBaseUrl/actuator/health"
    Wait-ForHealth -ServiceName "customer-service" -Uri "$customerBaseUrl/actuator/health"

    $comuna = Invoke-JsonRequest -Uri "$geoCatalogBaseUrl/api/v1/comunas/13119"
    if ([int]$comuna.codigo -ne 13119) {
        throw "El catalogo devolvio un codigo de comuna inesperado: $($comuna.codigo)."
    }
    if ([string]::IsNullOrWhiteSpace($comuna.nombre)) {
        throw "El catalogo devolvio una comuna sin nombre."
    }
    Write-Host "Catalogo geografico respondio comuna $($comuna.codigo) ($($comuna.nombre))."

    $cliente = Invoke-JsonRequest `
        -Uri "$customerBaseUrl/api/v1/clientes" `
        -Method "Post" `
        -Body @{
            rut = "12345678-5"
            tipoCliente = "PERSONA_NATURAL"
            nombre = "Smoke Test Docker Compose"
            telefonos = @("+56912345678")
            correos = @("smoke-test@example.com")
            condicionPago = "CONTADO"
            prioridadComercial = "ESTANDAR"
        }

    if ([string]::IsNullOrWhiteSpace($cliente.id)) {
        throw "La creacion del cliente no devolvio un identificador."
    }

    $direccion = Invoke-JsonRequest `
        -Uri "$customerBaseUrl/api/v1/clientes/$($cliente.id)/direcciones" `
        -Method "Post" `
        -Body @{
            calle = "Avenida Los Aromos"
            numero = "123"
            codigoComuna = "13119"
            latitud = -33.511
            longitud = -70.762
            referencia = "Smoke test"
            marcarComoPrincipal = $true
        }

    if ([string]::IsNullOrWhiteSpace($direccion.id)) {
        throw "La creacion de la direccion no devolvio un identificador."
    }

    Write-Host "Smoke test end-to-end exitoso: catalogo, cliente y direccion integrados."
    exit 0
} finally {
    Invoke-Compose -Arguments @("down", "--volumes", "--remove-orphans")
    foreach ($name in $smokePorts.Keys) {
        if ($null -eq $originalEnvironment[$name]) {
            [Environment]::SetEnvironmentVariable($name, $null, "Process")
        } else {
            [Environment]::SetEnvironmentVariable($name, $originalEnvironment[$name], "Process")
        }
    }
}
