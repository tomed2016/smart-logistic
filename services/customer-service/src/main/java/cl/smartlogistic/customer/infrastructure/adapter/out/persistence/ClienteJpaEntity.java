package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cliente")
public class ClienteJpaEntity {

    @Id
    private UUID id;

    @Column(name = "rut_numero", nullable = false)
    private long rutNumero;

    @Column(name = "rut_dv", nullable = false, length = 1)
    private String rutDv;

    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private String tipoCliente;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cliente_telefono", joinColumns = @JoinColumn(name = "cliente_id"))
    @Column(name = "numero")
    private List<String> telefonos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cliente_correo", joinColumns = @JoinColumn(name = "cliente_id"))
    @Column(name = "direccion_correo")
    private List<String> correos = new ArrayList<>();

    @Column(name = "condicion_pago", nullable = false, length = 20)
    private String condicionPago;

    @Column(name = "prioridad_comercial", nullable = false, length = 20)
    private String prioridadComercial;

    @Column(nullable = false, length = 20)
    private String estado;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DireccionJpaEntity> direcciones = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    protected ClienteJpaEntity() {
        // requerido por JPA
    }

    public ClienteJpaEntity(UUID id, long rutNumero, String rutDv, String tipoCliente, String nombre,
                             List<String> telefonos, List<String> correos, String condicionPago,
                             String prioridadComercial, String estado, Instant createdAt, String createdBy,
                             Instant updatedAt, String updatedBy) {
        this.id = id;
        this.rutNumero = rutNumero;
        this.rutDv = rutDv;
        this.tipoCliente = tipoCliente;
        this.nombre = nombre;
        this.telefonos = new ArrayList<>(telefonos);
        this.correos = new ArrayList<>(correos);
        this.condicionPago = condicionPago;
        this.prioridadComercial = prioridadComercial;
        this.estado = estado;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void agregarDireccion(DireccionJpaEntity direccion) {
        direccion.setCliente(this);
        this.direcciones.add(direccion);
    }

    public void reemplazarDatosBasicos(String nombre, List<String> telefonos, List<String> correos,
                                        String condicionPago, String prioridadComercial, String estado,
                                        Instant updatedAt, String updatedBy) {
        this.nombre = nombre;
        this.telefonos = new ArrayList<>(telefonos);
        this.correos = new ArrayList<>(correos);
        this.condicionPago = condicionPago;
        this.prioridadComercial = prioridadComercial;
        this.estado = estado;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public UUID getId() {
        return id;
    }

    public long getRutNumero() {
        return rutNumero;
    }

    public String getRutDv() {
        return rutDv;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getTelefonos() {
        return telefonos;
    }

    public List<String> getCorreos() {
        return correos;
    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public String getPrioridadComercial() {
        return prioridadComercial;
    }

    public String getEstado() {
        return estado;
    }

    public List<DireccionJpaEntity> getDirecciones() {
        return direcciones;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
