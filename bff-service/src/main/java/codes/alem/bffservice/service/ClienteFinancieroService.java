package codes.alem.bffservice.service;

import codes.alem.bffservice.dto.ClienteConProductosDto;
import codes.alem.bffservice.dto.ClienteDto;
import codes.alem.bffservice.dto.ProductoFinancieroDto;
import codes.alem.bffservice.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView; // Importante usar ContextView

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteFinancieroService {

    private static final Logger EXTERNAL_CALLS = LoggerFactory.getLogger("EXTERNAL_CALLS");
    private static final String TRACKING_ID_KEY = "trackingId";

    private final WebClient webClient;
    private final EncryptionService encryptionService;
    private final ClienteMapper clienteMapper;

    public Mono<ClienteConProductosDto> obtenerClienteConProductos(String codigoUnicoEncriptado) {
        // 1. Usa deferContextual para "esperar" a que el contexto reactivo esté disponible
        return Mono.deferContextual(contextView -> {
            // 2. Extrae el trackingId del contexto, NO del MDC
            String trackingId = contextView.get(TRACKING_ID_KEY);
            String codigoUnico = encryptionService.decrypt(codigoUnicoEncriptado);

            EXTERNAL_CALLS.info("🔄 Consultando cliente y productos para código: {}", codigoUnico);

            Mono<ClienteDto> clienteMono = obtenerCliente(codigoUnico, trackingId);
            Flux<ProductoFinancieroDto> productosFlux = obtenerProductosFinancieros(codigoUnico, trackingId);

            return clienteMono.zipWith(productosFlux.collectList())
                    .map(tuple -> {
                        ClienteDto cliente = tuple.getT1();
                        List<ProductoFinancieroDto> productos = tuple.getT2();

                        EXTERNAL_CALLS.info("✨ Consolidando respuesta: {} productos para {}",
                                productos.size(), cliente.getNombres());

                        return clienteMapper.toClienteConProductos(cliente, productos);
                    });
        });
    }

    // 3. Modifica los métodos privados para que acepten el trackingId como parámetro
    private Mono<ClienteDto> obtenerCliente(String codigoUnico, String trackingId) {
        EXTERNAL_CALLS.debug("→ Llamando CLIENT-SERVICE");
        return webClient.get()
                .uri("http://client-service/api/clientes/{codigoUnico}", codigoUnico)
                .header("X-Tracking-ID", trackingId) // Ahora usas el parámetro
                .retrieve()
                .bodyToMono(ClienteDto.class)
                .doOnSuccess(cliente -> EXTERNAL_CALLS.debug("← CLIENT-SERVICE: OK"))
                .doOnError(error -> EXTERNAL_CALLS.error("← CLIENT-SERVICE: ERROR - {}", error.getMessage()));
    }

    private Flux<ProductoFinancieroDto> obtenerProductosFinancieros(String codigoUnico, String trackingId) {
        EXTERNAL_CALLS.debug("→ Llamando PRODUCT-SERVICE");
        return webClient.get()
                .uri("http://product-service/api/productos/{codigoUnico}", codigoUnico)
                .header("X-Tracking-ID", trackingId) // Y aquí también
                .retrieve()
                .bodyToFlux(ProductoFinancieroDto.class)
                .doOnComplete(() -> EXTERNAL_CALLS.debug("← PRODUCT-SERVICE: OK"))
                .doOnError(error -> EXTERNAL_CALLS.error("← PRODUCT-SERVICE: ERROR - {}", error.getMessage()));
    }
}