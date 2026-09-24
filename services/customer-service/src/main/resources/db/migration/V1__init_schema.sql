-- Bounded context: Clientes
-- Cada microservicio gestiona su propio esquema (Database per Service, ADR-001).

CREATE TABLE cliente (
    id                  UUID PRIMARY KEY,
    rut_numero          BIGINT NOT NULL,
    rut_dv              CHAR(1) NOT NULL,
    tipo_cliente        VARCHAR(20) NOT NULL,
    nombre              VARCHAR(200) NOT NULL,
    condicion_pago      VARCHAR(20) NOT NULL,
    prioridad_comercial VARCHAR(20) NOT NULL,
    estado              VARCHAR(20) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL,
    created_by          VARCHAR(100) NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    updated_by          VARCHAR(100) NOT NULL,
    CONSTRAINT uk_cliente_rut UNIQUE (rut_numero, rut_dv)
);

CREATE TABLE cliente_telefono (
    cliente_id UUID NOT NULL REFERENCES cliente (id) ON DELETE CASCADE,
    numero     VARCHAR(20) NOT NULL,
    PRIMARY KEY (cliente_id, numero)
);

CREATE TABLE cliente_correo (
    cliente_id       UUID NOT NULL REFERENCES cliente (id) ON DELETE CASCADE,
    direccion_correo VARCHAR(200) NOT NULL,
    PRIMARY KEY (cliente_id, direccion_correo)
);

CREATE TABLE direccion (
    id            UUID PRIMARY KEY,
    cliente_id    UUID NOT NULL REFERENCES cliente (id) ON DELETE CASCADE,
    calle         VARCHAR(200) NOT NULL,
    numero        VARCHAR(20) NOT NULL,
    comuna_codigo VARCHAR(50) NOT NULL,
    comuna_nombre VARCHAR(100) NOT NULL,
    region        VARCHAR(60) NOT NULL,
    latitud       DOUBLE PRECISION NOT NULL,
    longitud      DOUBLE PRECISION NOT NULL,
    referencia    VARCHAR(300),
    es_principal  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_direccion_cliente_id ON direccion (cliente_id);

-- Patron Transactional Outbox (ADR-002): el cambio de estado del agregado y el
-- registro del evento se persisten en la misma transaccion; un publicador
-- asincrono separado los envia a RabbitMQ y completa published_at.
CREATE TABLE outbox_event (
    id              UUID PRIMARY KEY,
    aggregate_id    VARCHAR(100) NOT NULL,
    event_type      VARCHAR(100) NOT NULL,
    schema_version  INT NOT NULL,
    payload         TEXT NOT NULL,
    occurred_on     TIMESTAMPTZ NOT NULL,
    published_at    TIMESTAMPTZ NULL
);

CREATE INDEX idx_outbox_event_pendientes ON outbox_event (occurred_on) WHERE published_at IS NULL;
