package codes.alem.productservice.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@Slf4j
@Order(-1) // Dale alta prioridad para que se ejecute primero
public class TrackingInterceptor implements WebFilter {

    private static final String TRACKING_ID_KEY = "trackingId";
    private static final String SERVICE_KEY = "service";
    private static final String SERVICE_NAME = "PRODUCT-SERVICE";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String trackingId = exchange.getRequest().getHeaders().getFirst("X-Tracking-ID");

        // Si no hay trackingId, puedes optar por generar uno
        // if (trackingId == null) {
        //     trackingId = "PROD-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        // }

        // La clave es no depender del MDC directamente, sino del Context de Reactor
        if (trackingId == null) {
            // Si no hay ID, simplemente continuamos la cadena sin enriquecer el contexto.
            return chain.filter(exchange);
        }

        // Ejecuta la cadena adjuntando el ID al Context de Reactor
        return chain.filter(exchange)
                // 1. "Pega" el ID al flujo reactivo. Ahora viaja con la petición.
                .contextWrite(ctx -> ctx.put(TRACKING_ID_KEY, trackingId))
                .doOnEach(signal -> {
                    // 2. Antes de CUALQUIER evento, recupera el ID del flujo y lo pone en el MDC del hilo actual.
                    // ¡Esta es la corrección más importante!
                    Context context = (Context) signal.getContextView();
                    if (context.hasKey(TRACKING_ID_KEY)) {
                        MDC.put(TRACKING_ID_KEY, context.get(TRACKING_ID_KEY));
                        MDC.put(SERVICE_KEY, SERVICE_NAME);
                    }
                })
                .doFinally(signalType -> {
                    // 3. Al final, limpia el MDC para evitar fugas entre peticiones.
                    MDC.clear();
                });
    }
}