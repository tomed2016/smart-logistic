package cl.smartlogistic.customer.domain.model;

/**
 * Prioridad comercial del cliente, usada por Planificacion Logistica para desempatar
 * asignacion de rutas/ventanas horarias cuando la capacidad es limitada.
 */
public enum PrioridadComercial {
    ESTANDAR,
    PREFERENTE,
    VIP
}
