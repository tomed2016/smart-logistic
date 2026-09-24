$rows = Import-Csv .\bdcut-cl-raw.csv -Encoding UTF8

function Esc($s) { $s.Replace("'", "''") }

$regiones = @{}
$provincias = @{}
$comunas = @()

foreach ($r in $rows) {
    $rid = [int]$r.REGION_ID
    $regiones[$rid] = $r.REGION_NOMBRE.Trim()
    $provId = [int]$r.PROVINCIA_ID
    $provincias[$provId] = @{ nombre = $r.PROVINCIA_NOMBRE.Trim(); region = $rid }
    $comunas += [PSCustomObject]@{ codigo = [int]$r.COMUNA_ID; nombre = $r.COMUNA_NOMBRE.Trim(); provincia = $provId }
}

$lines = @('-- Regiones oficiales de Chile (16), codigo = codigo region INE')
foreach ($rid in ($regiones.Keys | Sort-Object)) {
    $lines += "INSERT INTO region (codigo, nombre) VALUES ($rid, '$(Esc $regiones[$rid])');"
}
Set-Content -Path .\V1__seed_regiones.sql -Value $lines -Encoding UTF8

$lines = @('-- Provincias oficiales de Chile (56), codigo = codigo provincia INE')
foreach ($provId in ($provincias.Keys | Sort-Object)) {
    $p = $provincias[$provId]
    $lines += "INSERT INTO provincia (codigo, nombre, region_codigo) VALUES ($provId, '$(Esc $p.nombre)', $($p.region));"
}
Set-Content -Path .\V2__seed_provincias.sql -Value $lines -Encoding UTF8

$lines = @('-- Comunas oficiales de Chile (346), codigo = codigo comuna INE')
foreach ($c in ($comunas | Sort-Object codigo)) {
    $lines += "INSERT INTO comuna (codigo, nombre, provincia_codigo) VALUES ($($c.codigo), '$(Esc $c.nombre)', $($c.provincia));"
}
Set-Content -Path .\V3__seed_comunas.sql -Value $lines -Encoding UTF8

Write-Host "regiones=$($regiones.Count) provincias=$($provincias.Count) comunas=$($comunas.Count)"
