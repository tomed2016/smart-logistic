-- Bounded context: Catalogo Geografico CL
-- Catalogo de referencia de solo lectura (regiones, provincias, comunas oficiales).
-- Claves primarias naturales (codigo INE/SUBDERE): ver nota en RegionJpaEntity.

CREATE TABLE region (
    codigo INTEGER PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE provincia (
    codigo        INTEGER PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    region_codigo INTEGER NOT NULL REFERENCES region (codigo)
);

CREATE INDEX idx_provincia_region ON provincia (region_codigo);

CREATE TABLE comuna (
    codigo           INTEGER PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    provincia_codigo INTEGER NOT NULL REFERENCES provincia (codigo)
);

CREATE INDEX idx_comuna_provincia ON comuna (provincia_codigo);
CREATE INDEX idx_comuna_nombre ON comuna (lower(nombre));
