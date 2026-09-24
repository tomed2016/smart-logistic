package cl.smartlogistic.customer.infrastructure.adapter.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.smartlogistic.customer.domain.exception.ClienteNoEncontradoException;
import cl.smartlogistic.customer.domain.exception.RutDuplicadoException;
import cl.smartlogistic.customer.domain.model.Cliente;
import cl.smartlogistic.customer.domain.model.ClienteId;
import cl.smartlogistic.customer.domain.model.CondicionPago;
import cl.smartlogistic.customer.domain.model.Correo;
import cl.smartlogistic.customer.domain.model.PrioridadComercial;
import cl.smartlogistic.customer.domain.model.Telefono;
import cl.smartlogistic.customer.domain.model.TipoCliente;
import cl.smartlogistic.customer.domain.port.in.ActualizarClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.ActualizarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.AgregarDireccionUseCase;
import cl.smartlogistic.customer.domain.port.in.ConsultarClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.CrearClienteUseCase;
import cl.smartlogistic.customer.domain.port.in.DesactivarClienteUseCase;
import cl.smartlogistic.customer.domain.port.out.ClienteRepository;
import cl.smartlogistic.shared.rut.Rut;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrearClienteUseCase crearClienteUseCase;
    @MockBean
    private ActualizarClienteUseCase actualizarClienteUseCase;
    @MockBean
    private AgregarDireccionUseCase agregarDireccionUseCase;
    @MockBean
    private ActualizarDireccionUseCase actualizarDireccionUseCase;
    @MockBean
    private DesactivarClienteUseCase desactivarClienteUseCase;
    @MockBean
    private ConsultarClienteUseCase consultarClienteUseCase;

    @Test
    @DisplayName("POST /api/v1/clientes con datos validos retorna 201 y el id creado")
    void crearClienteRetorna201() throws Exception {
        ClienteId id = ClienteId.nuevo();
        when(crearClienteUseCase.ejecutar(any())).thenReturn(id);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "12345678-5",
                                  "tipoCliente": "PERSONA_NATURAL",
                                  "nombre": "Juan Perez",
                                  "telefonos": ["+56912345678"],
                                  "correos": [],
                                  "condicionPago": "CONTADO",
                                  "prioridadComercial": "ESTANDAR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.value().toString()));
    }

    @Test
    @DisplayName("POST /api/v1/clientes sin nombre retorna 400 por validacion")
    void crearClienteSinNombreRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "12345678-5",
                                  "tipoCliente": "PERSONA_NATURAL",
                                  "nombre": "",
                                  "telefonos": ["+56912345678"],
                                  "condicionPago": "CONTADO",
                                  "prioridadComercial": "ESTANDAR"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/clientes con RUT duplicado retorna 409")
    void crearClienteConRutDuplicadoRetorna409() throws Exception {
        when(crearClienteUseCase.ejecutar(any())).thenThrow(new RutDuplicadoException("12345678-5"));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "rut": "12345678-5",
                                  "tipoCliente": "PERSONA_NATURAL",
                                  "nombre": "Juan Perez",
                                  "telefonos": ["+56912345678"],
                                  "condicionPago": "CONTADO",
                                  "prioridadComercial": "ESTANDAR"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} con cliente existente retorna 200 con sus datos")
    void obtenerClienteExistenteRetorna200() throws Exception {
        Cliente cliente = clienteDePrueba();
        when(consultarClienteUseCase.obtenerPorId(cliente.id().value().toString())).thenReturn(cliente);

        mockMvc.perform(get("/api/v1/clientes/{id}", cliente.id().value().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("GET /api/v1/clientes/{id} con cliente inexistente retorna 404")
    void obtenerClienteInexistenteRetorna404() throws Exception {
        when(consultarClienteUseCase.obtenerPorId(anyString()))
                .thenThrow(new ClienteNoEncontradoException("id-inexistente"));

        mockMvc.perform(get("/api/v1/clientes/{id}", "id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/clientes retorna una pagina de clientes")
    void listarClientesRetornaPagina() throws Exception {
        Cliente cliente = clienteDePrueba();
        when(consultarClienteUseCase.listar(any(), any(), org.mockito.ArgumentMatchers.eq(0),
                org.mockito.ArgumentMatchers.eq(20)))
                .thenReturn(new ClienteRepository.PaginaClientes(List.of(cliente), 1, 0, 20));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElementos").value(1))
                .andExpect(jsonPath("$.contenido[0].nombre").value("Juan Perez"));
    }

    private static Cliente clienteDePrueba() {
        Cliente cliente = Cliente.crear(
                Rut.of("12345678-5"), TipoCliente.PERSONA_NATURAL, "Juan Perez",
                List.of(new Telefono("+56912345678")), List.of(new Correo("juan@example.com")),
                CondicionPago.CONTADO, PrioridadComercial.ESTANDAR, "admin", Instant.now());
        cliente.eventosPendientes();
        return cliente;
    }
}
