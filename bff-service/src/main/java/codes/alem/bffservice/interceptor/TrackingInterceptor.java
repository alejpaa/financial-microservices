package codes.alem.bffservice.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.UUID;

@Component
@Order(-1) // La más alta prioridad
@Slf4j
public class TrackingInterceptor implements WebFilter {

    private static final Logger REQUEST_TRACKER = LoggerFactory.getLogger("REQUEST_TRACKER");
    private static final String TRACKING_ID_KEY = "trackingId";
    private static final String SERVICE_KEY = "service";
    private static final String SERVICE_NAME = "BFF-SERVICE";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Obtener o generar el trackingId
        String trackingId = exchange.getRequest().getHeaders().getFirst("X-Tracking-ID");
        if (trackingId == null) {
            trackingId = "BFF-" + UUID.randomUUID().toString().substring(0, 8);
        }

        // 2. Poblar el MDC para el hilo INICIAL
        MDC.put(TRACKING_ID_KEY, trackingId);
        MDC.put(SERVICE_KEY, SERVICE_NAME);

        // 3. Escribir el log INCOMING AHORA, mientras el MDC está poblado
        String method = exchange.getRequest().getMethod().toString();
        String path = exchange.getRequest().getPath().value();
        REQUEST_TRACKER.info("📥 INCOMING {} {}", method, path);

        // 4. Ejecutar la cadena con el contexto para los hilos SIGUIENTES
        String finalTrackingId = trackingId;
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(TRACKING_ID_KEY, finalTrackingId))
                .doOnEach(signal -> {
                    // 5. Restaurar el MDC en cada cambio de hilo
                    ContextView context = signal.getContextView();
                    if (context.hasKey(TRACKING_ID_KEY)) {
                        MDC.put(TRACKING_ID_KEY, context.get(TRACKING_ID_KEY));
                        MDC.put(SERVICE_KEY, SERVICE_NAME);
                    }
                })
                .doOnSuccess(unused -> REQUEST_TRACKER.info("📤 OUTGOING response"))
                .doOnError(error -> REQUEST_TRACKER.error("💥 OUTGOING error: {}", error.getMessage()))
                .doFinally(signalType -> {
                    // 6. Limpiar el MDC al final de todo
                    MDC.clear();
                });
    }
}