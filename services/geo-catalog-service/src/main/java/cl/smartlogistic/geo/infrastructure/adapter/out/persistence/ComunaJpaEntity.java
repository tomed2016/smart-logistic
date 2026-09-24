package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA para el catalogo de comunas (clave primaria natural: codigo INE, ver
 * {@link RegionJpaEntity}). {@code regionCodigo} se desnormaliza para consultas
 * frecuentes por region (ver {@code Comuna} en el modelo de dominio).
 */
@Entity
@Table(name = "comuna")
public class ComunaJpaEntity {

    @Id
    @Column(name = "codigo")
    private int codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "provincia_codigo", nullable = false)
    private int provinciaCodigo;

    protected ComunaJpaEntity() {
    }

    public ComunaJpaEntity(int codigo, String nombre, int provinciaCodigo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.provinciaCodigo = provinciaCodigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getProvinciaCodigo() {
        return provinciaCodigo;
    }
}
