package cl.smartlogistic.customer.domain.model;

/**
 * Estado del ciclo de vida del cliente. No existe eliminacion fisica: un cliente
 * pasa a {@code INACTIVO} preservando todo su historial comercial.
 */
public enum EstadoCliente {
    ACTIVO,
    INACTIVO
}
