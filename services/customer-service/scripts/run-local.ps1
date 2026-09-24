<#
Simple script to build and run customer-service in local profile with JVM flag
that avoids the JDK native-access warning (useful with JDK 21+).
Usage:
  .\run-local.ps1          # builds and runs with spring-boot:run
  .\run-local.ps1 -RunJar  # builds and runs the produced jar
#>
param(
    [switch]$RunJar
)

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $projectDir

Write-Host "Building project..."
mvn -DskipTests package
if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed. Aborting."
    exit $LASTEXITCODE
}

if ($RunJar) {
    Write-Host "Running jar with --enable-native-access=ALL-UNNAMED"
    & java --enable-native-access=ALL-UNNAMED -jar target\customer-service.jar --spring.profiles.active=local
} else {
    Write-Host "Running with mvn spring-boot:run (JVM args passed to plugin)"
    mvn spring-boot:run
}
