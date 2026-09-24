package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "direccion")
public class DireccionJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteJpaEntity cliente;

    @Column(nullable = false, length = 200)
    private String calle;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(name = "comuna_codigo", nullable = false, length = 50)
    private String comunaCodigo;

    @Column(name = "comuna_nombre", nullable = false, length = 100)
    private String comunaNombre;

    @Column(nullable = false, length = 60)
    private String region;

    @Column(nullable = false)
    private double latitud;

    @Column(nullable = false)
    private double longitud;

    @Column(length = 300)
    private String referencia;

    @Column(name = "es_principal", nullable = false)
    private boolean esPrincipal;

    protected DireccionJpaEntity() {
        // requerido por JPA
    }

    public DireccionJpaEntity(UUID id, ClienteJpaEntity cliente, String calle, String numero,
                               String comunaCodigo, String comunaNombre, String region,
                               double latitud, double longitud, String referencia, boolean esPrincipal) {
        this.id = id;
        this.cliente = cliente;
        this.calle = calle;
        this.numero = numero;
        this.comunaCodigo = comunaCodigo;
        this.comunaNombre = comunaNombre;
        this.region = region;
        this.latitud = latitud;
        this.longitud = longitud;
        this.referencia = referencia;
        this.esPrincipal = esPrincipal;
    }

    public UUID getId() {
        return id;
    }

    public ClienteJpaEntity getCliente() {
        return cliente;
    }

    public void setCliente(ClienteJpaEntity cliente) {
        this.cliente = cliente;
    }

    public String getCalle() {
        return calle;
    }

    public String getNumero() {
        return numero;
    }

    public String getComunaCodigo() {
        return comunaCodigo;
    }

    public String getComunaNombre() {
        return comunaNombre;
    }

    public String getRegion() {
        return region;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getReferencia() {
        return referencia;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }
}
