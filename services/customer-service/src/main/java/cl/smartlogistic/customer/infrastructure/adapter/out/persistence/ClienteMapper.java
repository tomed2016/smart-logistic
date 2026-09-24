package cl.smartlogistic.customer.infrastructure.adapter.out.persistence;

import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.model.Comuna;
import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.Coordenadas;
import cl.smartlogistic.customer.domain.model.Correo;
import cl.smartlogistic.customer.domain.model.Direccion;
import cl.smartlogistic.customer.domain.model.DireccionId;
import cl.smartlogistic.customer.domain.model.EstadoCliente;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;
import cl.smartlogistic.customer.domain.model.Region;
import cl.smartlogistic.customer.domain.model.Telefono;
import cl.smartlogistic.customer.domain.model.TipoCliente;
import cl.smartlogistic.shared.audit.AuditInfo;
import cl.smartlogistic.shared.rut.Rut;

/** Traduce entre el agregado de dominio {@link Cliente} y su representacion JPA. */
final class ClienteMapper {

    private ClienteMapper() {
    }

    static ClienteJpaEntity aEntidad(Cliente cliente) {
        Rut rut = cliente.rut();
        ClienteJpaEntity entidad = new ClienteJpaEntity(
                cliente.id().value(),
                rut.numero(),
                String.valueOf(rut.digitoVerificador()),
                cliente.tipoCliente().name(),
                cliente.nombre(),
                cliente.telefonos().stream().map(Telefono::numero).toList(),
                cliente.correos().stream().map(Correo::direccion).toList(),
                cliente.condicionPago().name(),
                cliente.prioridadComercial().name(),
                cliente.estado().name(),
                cliente.auditInfo().creadoEn(),
                cliente.auditInfo().creadoPor(),
                cliente.auditInfo().actualizadoEn(),
                cliente.auditInfo().actualizadoPor());

        for (Direccion direccion : cliente.direcciones()) {
            entidad.agregarDireccion(new DireccionJpaEntity(
                    direccion.id().value(),
                    entidad,
                    direccion.calle(),
                    direccion.numero(),
                    direccion.comuna().codigo(),
                    direccion.comuna().nombre(),
                    direccion.comuna().region().name(),
                    direccion.coordenadas().latitud(),
                    direccion.coordenadas().longitud(),
                    direccion.referencia(),
                    direccion.esPrincipal()));
        }
        return entidad;
    }

    static Cliente aDominio(ClienteJpaEntity entidad) {
        Rut rut = Rut.of(entidad.getRutNumero() + "-" + entidad.getRutDv());
        return Cliente.reconstruir(
                ClienteId.de(entidad.getId()),
                rut,
                TipoCliente.valueOf(entidad.getTipoCliente()),
                entidad.getNombre(),
                entidad.getTelefonos().stream().map(Telefono::new).toList(),
                entidad.getCorreos().stream().map(Correo::new).toList(),
                CondicionPago.valueOf(entidad.getCondicionPago()),
                PrioridadComercial.valueOf(entidad.getPrioridadComercial()),
                EstadoCliente.valueOf(entidad.getEstado()),
                entidad.getDirecciones().stream().map(ClienteMapper::aDireccionDominio).toList(),
                AuditInfo.crear(entidad.getCreatedBy(), entidad.getCreatedAt())
                        .actualizar(entidad.getUpdatedBy(), entidad.getUpdatedAt()));
    }

    private static Direccion aDireccionDominio(DireccionJpaEntity entidad) {
        Comuna comuna = new Comuna(entidad.getComunaCodigo(), entidad.getComunaNombre(),
                Region.valueOf(entidad.getRegion()));
        return Direccion.reconstruir(
                DireccionId.de(entidad.getId()),
                entidad.getCalle(),
                entidad.getNumero(),
                comuna,
                new Coordenadas(entidad.getLatitud(), entidad.getLongitud()),
                entidad.getReferencia(),
                entidad.isEsPrincipal());
    }
}
