package codes.alem.bffservice.controller;

import codes.alem.bffservice.dto.ClienteConProductosDto;
import codes.alem.bffservice.dto.ProductoFinancieroDto;
import codes.alem.bffservice.service.ClienteFinancieroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteFinancieroControllerTest {

    @Mock
    private ClienteFinancieroService clienteFinancieroService;

    @InjectMocks
    private ClienteFinancieroController controller;

    private ClienteConProductosDto clienteConProductosDto;
    private String codigoEncriptado;

    @BeforeEach
    void setUp() {
        codigoEncriptado = "codigo_encriptado_test";

        // Crear productos de prueba
        ProductoFinancieroDto producto1 = new ProductoFinancieroDto();
        producto1.setTipoProducto("Cuenta de Ahorro");
        producto1.setNombre("Ahorro Premium");
        producto1.setSaldo(BigDecimal.valueOf(5000.50));

        ProductoFinancieroDto producto2 = new ProductoFinancieroDto();
        producto2.setTipoProducto("Tarjeta de Crédito");
        producto2.setNombre("Visa Gold");
        producto2.setSaldo(BigDecimal.valueOf(2500.75));

        List<ProductoFinancieroDto> productos = Arrays.asList(producto1, producto2);

        // Crear cliente con productos
        clienteConProductosDto = new ClienteConProductosDto();
        clienteConProductosDto.setNombres("Juan Carlos");
        clienteConProductosDto.setApellidos("Pérez García");
        clienteConProductosDto.setTipoDocumento("DNI");
        clienteConProductosDto.setNumeroDocumento("12345678");
        clienteConProductosDto.setProductosFinancieros(productos);
    }

    @Test
    void obtenerClienteConProductos_CuandoEsExitoso_DebeRetornarOk() {
        // Given
        when(clienteFinancieroService.obtenerClienteConProductos(anyString()))
                .thenReturn(Mono.just(clienteConProductosDto));

        // When
        Mono<ResponseEntity<ClienteConProductosDto>> resultado =
                controller.obtenerClienteConProductos(codigoEncriptado);

        // Then
        StepVerifier.create(resultado)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertEquals("Juan Carlos", response.getBody().getNombres());
                    assertEquals("Pérez García", response.getBody().getApellidos());
                    assertEquals("DNI", response.getBody().getTipoDocumento());
                    assertEquals("12345678", response.getBody().getNumeroDocumento());
                    assertEquals(2, response.getBody().getProductosFinancieros().size());
                })
                .verifyComplete();
    }

    @Test
    void obtenerClienteConProductos_CuandoOcurreIllegalArgumentException_DebeRetornarBadRequest() {
        // Given
        when(clienteFinancieroService.obtenerClienteConProductos(anyString()))
                .thenReturn(Mono.error(new IllegalArgumentException("Código inválido")));

        // When
        Mono<ResponseEntity<ClienteConProductosDto>> resultado =
                controller.obtenerClienteConProductos(codigoEncriptado);

        // Then
        StepVerifier.create(resultado)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                    assertNull(response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void obtenerClienteConProductos_CuandoOcurreOtroError_DebeRetornarInternalServerError() {
        // Given
        when(clienteFinancieroService.obtenerClienteConProductos(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Error interno")));

        // When
        Mono<ResponseEntity<ClienteConProductosDto>> resultado =
                controller.obtenerClienteConProductos(codigoEncriptado);

        // Then
        StepVerifier.create(resultado)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertNull(response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void obtenerClienteConProductos_CuandoCodigoEsNull_DebeRetornarInternalServerError() {
        // Given
        when(clienteFinancieroService.obtenerClienteConProductos(null))
                .thenReturn(Mono.error(new NullPointerException("Código es null")));

        // When
        Mono<ResponseEntity<ClienteConProductosDto>> resultado =
                controller.obtenerClienteConProductos(null);

        // Then
        StepVerifier.create(resultado)
                .assertNext(response -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                })
                .verifyComplete();
    }
}
