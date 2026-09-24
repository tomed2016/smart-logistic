package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA para el catalogo de provincias (clave primaria natural: codigo INE,
 * ver {@link RegionJpaEntity}).
 */
@Entity
@Table(name = "provincia")
public class ProvinciaJpaEntity {

    @Id
    @Column(name = "codigo")
    private int codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "region_codigo", nullable = false)
    private int regionCodigo;

    protected ProvinciaJpaEntity() {
    }

    public ProvinciaJpaEntity(int codigo, String nombre, int regionCodigo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.regionCodigo = regionCodigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getRegionCodigo() {
        return regionCodigo;
    }
}
