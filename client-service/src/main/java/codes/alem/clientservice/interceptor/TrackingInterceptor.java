package codes.alem.clientservice.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TrackingInterceptor implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String trackingId = exchange.getRequest().getHeaders().getFirst("X-Tracking-ID");
        String correlationId = exchange.getRequest().getHeaders().getFirst("X-Correlation-ID");

        // Configurar MDC para logging automático
        if (trackingId != null) {
            MDC.put("trackingId", trackingId);
        }
        if (correlationId != null) {
            MDC.put("correlationId", correlationId);
        }
        MDC.put("service", "CLIENT-SERVICE");

        return chain.filter(exchange)
                .doFinally(signal -> MDC.clear());
    }
}
