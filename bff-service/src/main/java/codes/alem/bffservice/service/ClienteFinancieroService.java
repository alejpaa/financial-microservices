package codes.alem.bffservice.service;

import codes.alem.bffservice.dto.ClienteConProductosDto;
import codes.alem.bffservice.dto.ClienteDto;
import codes.alem.bffservice.dto.ProductoFinancieroDto;
import codes.alem.bffservice.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteFinancieroService {

    private static final String TRACKING_ID_KEY = "trackingId";
    private final WebClient webClient;
    private final EncryptionService encryptionService;
    private final ClienteMapper clienteMapper;

    public Mono<ClienteConProductosDto> obtenerClienteConProductos(String codigoUnicoEncriptado) {
        return Mono.deferContextual(contextView -> {
            // Extraer trackingId del Reactor Context
            String trackingId = contextView.get(TRACKING_ID_KEY);

            // Configurar el MDC para logs dentro de este bloque
            MDC.put(TRACKING_ID_KEY, trackingId);
            log.info("🔄 Iniciando consolidación de cliente y productos");

            // 3️⃣ Lógica principal
            String codigoUnico = encryptionService.decrypt(codigoUnicoEncriptado);
            Mono<ClienteDto> clienteMono = obtenerCliente(codigoUnico);
            Flux<ProductoFinancieroDto> productosFlux = obtenerProductosFinancieros(codigoUnico);

            return clienteMono.zipWith(productosFlux.collectList())
                    .map(tuple -> {
                        ClienteDto cliente = tuple.getT1();
                        List<ProductoFinancieroDto> productos = tuple.getT2();

                        log.info(" Consolidando {} productos", productos.size());
                        return clienteMapper.toClienteConProductos(cliente, productos);
                    })
                    .doFinally(signalType -> MDC.clear());
        });
    }

    private Mono<ClienteDto> obtenerCliente(String codigoUnico) {
        return Mono.deferContextual(ctx -> {
            String trackingId = ctx.get(TRACKING_ID_KEY);
            MDC.put(TRACKING_ID_KEY, trackingId);
            log.debug("→ Llamando CLIENT-SERVICE");

            return webClient.get()
                    .uri("http://client-service/api/clientes/{codigoUnico}", codigoUnico)
                    .header("X-Tracking-ID", trackingId)
                    .retrieve()
                    .bodyToMono(ClienteDto.class)
                    .doOnEach(sig -> restoreMdcFromContext(ctx))
                    .doOnSuccess(c -> log.info("← CLIENT-SERVICE OK"))
                    .doOnError(e -> log.error("← CLIENT-SERVICE ERROR  {}", e.getMessage()))
                    .doFinally(st -> MDC.clear());
        });
    }

    private Flux<ProductoFinancieroDto> obtenerProductosFinancieros(String codigoUnico) {
        return Flux.deferContextual(ctx -> {
            String trackingId = ctx.get(TRACKING_ID_KEY);
            MDC.put(TRACKING_ID_KEY, trackingId);
            log.debug("→ Llamando PRODUCT-SERVICE");

            return webClient.get()
                    .uri("http://product-service/api/productos/{codigoUnico}", codigoUnico)
                    .header("X-Tracking-ID", trackingId)
                    .retrieve()
                    .bodyToFlux(ProductoFinancieroDto.class)
                    .doOnEach(sig -> restoreMdcFromContext(ctx))
                    .doOnComplete(() -> log.info("← PRODUCT-SERVICE OK"))
                    .doOnError(e -> log.error("← PRODUCT-SERVICE ERROR [{}] {}", trackingId, e.getMessage()))
                    .doFinally(st -> MDC.clear());
        });
    }

    private void restoreMdcFromContext(ContextView ctx) {
        ctx.<String>getOrEmpty(TRACKING_ID_KEY)
                .ifPresent(id -> MDC.put(TRACKING_ID_KEY, id));
    }
}
