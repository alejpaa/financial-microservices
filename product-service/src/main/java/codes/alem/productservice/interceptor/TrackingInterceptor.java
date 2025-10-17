package codes.alem.productservice.interceptor;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class TrackingInterceptor implements WebFilter {

    private static final String TRACKING_ID_KEY = "trackingId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String trackingId = exchange.getRequest().getHeaders().getFirst("X-Tracking-ID");

        // Si el BFF no envía un ID, no hacemos nada.
        if (trackingId == null || trackingId.isBlank()) {
            return chain.filter(exchange);
        }

        // Simplemente ejecutamos la cadena, "pegando" el trackingId al contexto reactivo.
        // El LoggingAspect se encargará del resto.
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(TRACKING_ID_KEY, trackingId));
    }
}