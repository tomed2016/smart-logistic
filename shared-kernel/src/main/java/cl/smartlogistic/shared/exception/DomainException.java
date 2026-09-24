package cl.smartlogistic.shared.exception;

/**
 * Excepcion base para violaciones de reglas de negocio o invariantes de dominio.
 * Todas las excepciones especificas de un bounded context deberian heredar de esta
 * clase (o de una subclase propia del servicio) para permitir un manejo uniforme
 * en los adaptadores de entrada (ej. mapeo a HTTP 4xx en los controladores REST).
 */
public abstract class DomainException extends RuntimeException {

    /** Generated serialVersionUID to make serialization explicit and avoid Sonar warnings. */
    private static final long serialVersionUID = 2026092401L;

	protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
