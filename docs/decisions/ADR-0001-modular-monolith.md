# ADR-0001: Modular Monolith First

## Status
Accepted

## Context
El equipo inicial es pequeño, el dominio tiene alta complejidad transaccional y el repositorio parte sin base técnica.

## Decision
Adoptar un monolito modular con DDD, arquitectura hexagonal, puertos de dominio y eventos internos.

## Consequences
- Menor complejidad operativa en la etapa inicial.
- Mejor velocidad para iterar sobre reglas de negocio críticas.
- Preparación explícita para extraer microservicios posteriormente sin reescribir el dominio.
