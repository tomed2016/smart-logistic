package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA para el catalogo de regiones. Usa el codigo oficial INE como clave
 * primaria natural (en lugar de un UUID generado) porque este servicio modela un
 * catalogo de referencia estatico administrado por el estado chileno, no un
 * agregado transaccional del negocio; el codigo INE ya es un identificador
 * externo estable y nunca cambia para una region existente.
 */
@Entity
@Table(name = "region")
public class RegionJpaEntity {

    @Id
    @Column(name = "codigo")
    private int codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    protected RegionJpaEntity() {
    }

    public RegionJpaEntity(int codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
