package cl.smartlogistic.shared.audit;

import java.time.Instant;
import java.util.Objects;

/**
 * Value Object que registra metadatos de auditoria de un agregado: quien y cuando lo
 * creo, y quien/cuando fue su ultima modificacion. Requisito transversal del sistema:
 * "todo cambio debe quedar auditado" y "no sobrescribir informacion historica" (la
 * creacion original nunca se pierde, solo se agregan datos de la ultima edicion).
 *
 * <p>Inmutable: cada modificacion produce una nueva instancia via {@link #actualizar}.</p>
 */
public final class AuditInfo {

    private static final String USER_NOT_NULL = "usuario no puede ser nulo";
    private static final String INSTANT_NOT_NULL = "instante no puede ser nulo";

    private final Instant creadoEn;
    private final String creadoPor;
    private final Instant actualizadoEn;
    private final String actualizadoPor;

    private AuditInfo(Instant creadoEn, String creadoPor, Instant actualizadoEn, String actualizadoPor) {
        this.creadoEn = creadoEn;
        this.creadoPor = creadoPor;
        this.actualizadoEn = actualizadoEn;
        this.actualizadoPor = actualizadoPor;
    }

    public static AuditInfo crear(String usuario, Instant instante) {
        Objects.requireNonNull(usuario, USER_NOT_NULL);
        Objects.requireNonNull(instante, INSTANT_NOT_NULL);
        return new AuditInfo(instante, usuario, instante, usuario);
    }

    public AuditInfo actualizar(String usuario, Instant instante) {
        Objects.requireNonNull(usuario, USER_NOT_NULL);
        Objects.requireNonNull(instante, INSTANT_NOT_NULL);
        return new AuditInfo(this.creadoEn, this.creadoPor, instante, usuario);
    }

    public Instant creadoEn() {
        return creadoEn;
    }

    public String creadoPor() {
        return creadoPor;
    }

    public Instant actualizadoEn() {
        return actualizadoEn;
    }

    public String actualizadoPor() {
        return actualizadoPor;
    }
}
