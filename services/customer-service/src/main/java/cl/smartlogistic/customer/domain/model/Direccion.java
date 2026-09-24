package cl.smartlogistic.customer.domain.model;

import java.util.Objects;

/**
 * Entidad interna del agregado {@link Cliente}: un lugar de entrega con comuna y
 * coordenadas geograficas. Solo es mutable a traves de metodos del agregado raiz
 * {@code Cliente}, nunca directamente.
 */
public final class Direccion {

    private final DireccionId id;
    private String calle;
    private String numero;
    private Comuna comuna;
    private Coordenadas coordenadas;
    private String referencia;
    private boolean esPrincipal;

    private Direccion(DireccionId id, String calle, String numero, Comuna comuna,
                       Coordenadas coordenadas, String referencia, boolean esPrincipal) {
        this.id = Objects.requireNonNull(id, "id de direccion no puede ser nulo");
        this.calle = requireNoBlank(calle, "calle");
        this.numero = requireNoBlank(numero, "numero");
        this.comuna = Objects.requireNonNull(comuna, "comuna no puede ser nula");
        this.coordenadas = Objects.requireNonNull(coordenadas, "coordenadas no pueden ser nulas");
        this.referencia = referencia;
        this.esPrincipal = esPrincipal;
    }

    static Direccion crear(String calle, String numero, Comuna comuna, Coordenadas coordenadas,
                            String referencia, boolean esPrincipal) {
        return new Direccion(DireccionId.nuevo(), calle, numero, comuna, coordenadas, referencia, esPrincipal);
    }

    /** Reconstruccion desde persistencia: no aplica invariantes de creacion, se asume ya validado. */
    public static Direccion reconstruir(DireccionId id, String calle, String numero, Comuna comuna,
                                         Coordenadas coordenadas, String referencia, boolean esPrincipal) {
        return new Direccion(id, calle, numero, comuna, coordenadas, referencia, esPrincipal);
    }

    void actualizarDatos(String calle, String numero, Comuna comuna, Coordenadas coordenadas, String referencia) {
        this.calle = requireNoBlank(calle, "calle");
        this.numero = requireNoBlank(numero, "numero");
        this.comuna = Objects.requireNonNull(comuna, "comuna no puede ser nula");
        this.coordenadas = Objects.requireNonNull(coordenadas, "coordenadas no pueden ser nulas");
        this.referencia = referencia;
    }

    void marcarComoPrincipal() {
        this.esPrincipal = true;
    }

    void desmarcarComoPrincipal() {
        this.esPrincipal = false;
    }

    private static String requireNoBlank(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar en blanco");
        }
        return valor;
    }

    public DireccionId id() {
        return id;
    }

    public String calle() {
        return calle;
    }

    public String numero() {
        return numero;
    }

    public Comuna comuna() {
        return comuna;
    }

    public Coordenadas coordenadas() {
        return coordenadas;
    }

    public String referencia() {
        return referencia;
    }

    public boolean esPrincipal() {
        return esPrincipal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Direccion direccion)) return false;
        return id.equals(direccion.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
