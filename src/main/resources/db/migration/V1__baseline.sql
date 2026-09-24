CREATE TABLE IF NOT EXISTS customer (
    id UUID PRIMARY KEY,
    business_identifier VARCHAR(32) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS vehicle (
    id UUID PRIMARY KEY,
    plate VARCHAR(32) NOT NULL,
    max_bottles INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY,
    sku VARCHAR(64) NOT NULL,
    name VARCHAR(255) NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory_item (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    available_units INTEGER NOT NULL,
    empty_bottles INTEGER NOT NULL,
    damaged_bottles INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS sales_order (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    scheduled_date DATE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS recurrence_rule (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    frequency VARCHAR(32) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS route_plan (
    id UUID PRIMARY KEY,
    planning_date DATE NOT NULL,
    vehicle_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS payment (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
