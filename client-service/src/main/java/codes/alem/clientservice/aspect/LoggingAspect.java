package codes.alem.clientservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String TRACKING_ID_KEY = "trackingId";
    private static final String SERVICE_KEY = "service";
    private static final String SERVICE_NAME = "CLIENT-SERVICE";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public void reactiveComponents() {}

    @Around("reactiveComponents()")
    public Object logAroundReactive(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();

        Object result = pjp.proceed();

        if (result instanceof Mono<?> mono) {
            return Mono.deferContextual(ctxView -> {
                String trackingId = ctxView.getOrDefault(TRACKING_ID_KEY, "N/A");
                MDC.put(TRACKING_ID_KEY, trackingId);
                MDC.put(SERVICE_KEY, SERVICE_NAME);

                log.info("▶️ START {}", method);

                return mono
                        .doOnSuccess(v -> {
                            MDC.put(TRACKING_ID_KEY, trackingId);
                            MDC.put(SERVICE_KEY, SERVICE_NAME);
                            log.info("✅ END {} ({}ms)", method, System.currentTimeMillis() - start);
                        })
                        .doOnError(e -> {
                            MDC.put(TRACKING_ID_KEY, trackingId);
                            MDC.put(SERVICE_KEY, SERVICE_NAME);
                            log.error("💥 ERROR {} - {}", method, e.getMessage());
                        })
                        .doFinally(st -> MDC.clear());
            });
        }

        if (result instanceof Flux<?> flux) {
            return Flux.deferContextual(ctxView -> {
                String trackingId = ctxView.getOrDefault(TRACKING_ID_KEY, "N/A");
                MDC.put(TRACKING_ID_KEY, trackingId);
                MDC.put(SERVICE_KEY, SERVICE_NAME);

                log.info("▶️ START {}", method);

                return flux
                        .doOnComplete(() -> {
                            MDC.put(TRACKING_ID_KEY, trackingId);
                            MDC.put(SERVICE_KEY, SERVICE_NAME);
                            log.info("✅ END {} ({}ms)", method, System.currentTimeMillis() - start);
                        })
                        .doOnError(e -> {
                            MDC.put(TRACKING_ID_KEY, trackingId);
                            MDC.put(SERVICE_KEY, SERVICE_NAME);
                            log.error("💥 ERROR {} - {}", method, e.getMessage());
                        })
                        .doFinally(st -> MDC.clear());
            });
        }

        // Métodos no reactivos
        MDC.put(SERVICE_KEY, SERVICE_NAME);
        MDC.put(TRACKING_ID_KEY, "N/A");

        log.info("▶️ START {}", method);
        try {
            Object resultSync = pjp.proceed();
            log.info("✅ END {} ({}ms)", method, System.currentTimeMillis() - start);
            return resultSync;
        } catch (Exception e) {
            log.error("💥 ERROR {} - {}", method, e.getMessage());
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
