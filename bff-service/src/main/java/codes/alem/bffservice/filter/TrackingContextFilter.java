package codes.alem.bffservice.filter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
@Order(-1)
public class TrackingContextFilter implements WebFilter {

    private static final String TRACKING_ID_KEY = "trackingId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String trackingId = exchange.getRequest().getHeaders().getFirst("X-Tracking-ID");
        if (trackingId == null) {
            trackingId = "BFF-" + UUID.randomUUID().toString().substring(0, 8);
        }

        // Log de entrada
        MDC.put(TRACKING_ID_KEY, trackingId);
//        System.out.printf("📥 INCOMING %s %s [%s]%n",
//                exchange.getRequest().getMethod(), exchange.getRequest().getPath(), trackingId);

        // Propagar en Reactor Context
        String finalTrackingId = trackingId;
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(TRACKING_ID_KEY, finalTrackingId))
                .doFinally(signalType -> MDC.clear());
    }
}
