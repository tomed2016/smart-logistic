# ADR-0002: Java Runtime Baseline

## Status
Accepted

## Context
La estrategia objetivo prefiere Java 21, pero el runner actual de CI del repositorio expone Java 17.

## Decision
Usar Java 17 como baseline compilable del bootstrap y mantener la migración a Java 21 como siguiente mejora de plataforma cuando el entorno de build lo soporte de forma homogénea.

## Consequences
- El proyecto compila en el entorno actual sin pasos manuales adicionales.
- Se evita bloquear la primera iteración arquitectónica.
- La evolución a Java 21 queda explícitamente registrada y acotada.
