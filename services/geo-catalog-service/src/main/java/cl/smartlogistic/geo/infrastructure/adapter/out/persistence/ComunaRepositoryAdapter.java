package cl.smartlogistic.geo.infrastructure.adapter.out.persistence;

import cl.smartlogistic.geo.domain.model.Comuna;
import cl.smartlogistic.geo.domain.port.out.ComunaRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ComunaRepositoryAdapter implements ComunaRepositoryPort {

    private final ComunaJpaRepository comunaJpaRepository;
    private final ProvinciaJpaRepository provinciaJpaRepository;

    public ComunaRepositoryAdapter(ComunaJpaRepository comunaJpaRepository,
                                    ProvinciaJpaRepository provinciaJpaRepository) {
        this.comunaJpaRepository = comunaJpaRepository;
        this.provinciaJpaRepository = provinciaJpaRepository;
    }

    @Override
    public List<Comuna> listarPorProvincia(int provinciaCodigo) {
        return comunaJpaRepository.findAllByProvinciaCodigoOrderByCodigoAsc(provinciaCodigo).stream()
                .map(entity -> toDomain(entity, regionCodigoDeProvincia(entity.getProvinciaCodigo())))
                .toList();
    }

    @Override
    public List<Comuna> listarPorRegion(int regionCodigo) {
        return comunaJpaRepository.findAllByRegionCodigo(regionCodigo).stream()
                .map(entity -> toDomain(entity, regionCodigo))
                .toList();
    }

    @Override
    public List<Comuna> buscarPorNombre(String nombreParcial) {
        Map<Integer, Integer> cacheRegionPorProvincia = new java.util.HashMap<>();
        return comunaJpaRepository.findAllByNombreContainingIgnoreCase(nombreParcial).stream()
                .map(entity -> toDomain(entity,
                        cacheRegionPorProvincia.computeIfAbsent(entity.getProvinciaCodigo(), this::regionCodigoDeProvincia)))
                .toList();
    }

    @Override
    public Optional<Comuna> buscarPorCodigo(int codigo) {
        return comunaJpaRepository.findById(codigo)
                .map(entity -> toDomain(entity, regionCodigoDeProvincia(entity.getProvinciaCodigo())));
    }

    @Override
    public List<Comuna> listarTodas() {
        Map<Integer, Integer> cacheRegionPorProvincia = new java.util.HashMap<>();
        return comunaJpaRepository.findAllByOrderByCodigoAsc().stream()
                .map(entity -> toDomain(entity,
                        cacheRegionPorProvincia.computeIfAbsent(entity.getProvinciaCodigo(), this::regionCodigoDeProvincia)))
                .toList();
    }

    private int regionCodigoDeProvincia(int provinciaCodigo) {
        return provinciaJpaRepository.findById(provinciaCodigo)
                .map(ProvinciaJpaEntity::getRegionCodigo)
                .orElseThrow(() -> new IllegalStateException(
                        "Inconsistencia de catalogo: provincia %d referenciada por una comuna no existe"
                                .formatted(provinciaCodigo)));
    }

    private Comuna toDomain(ComunaJpaEntity entity, int regionCodigo) {
        return new Comuna(entity.getCodigo(), entity.getNombre(), entity.getProvinciaCodigo(), regionCodigo);
    }
}
