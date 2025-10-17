package codes.alem.bffservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String TRACKING_ID_KEY = "trackingId";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logAroundReactive(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String method = pjp.getSignature().toShortString();

        Object result = pjp.proceed();

        if (result instanceof Mono<?> mono) {
            return mono
                    .doOnEach(signal -> {
                        ContextView ctx = signal.getContextView();
                        ctx.<String>getOrEmpty(TRACKING_ID_KEY)
                                .ifPresent(id -> MDC.put(TRACKING_ID_KEY, id));
                    })
                    .doOnSubscribe(s -> log.info("▶️ START {}", method))
                    .doOnSuccess(v -> log.info("✅ END {} ({}ms)", method, System.currentTimeMillis() - start))
                    .doOnError(e -> log.error("💥 ERROR {} - {}", method, e.getMessage()))
                    .doFinally(st -> MDC.clear());
        }

        if (result instanceof Flux<?> flux) {
            return flux
                    .doOnEach(signal -> {
                        ContextView ctx = signal.getContextView();
                        ctx.<String>getOrEmpty(TRACKING_ID_KEY)
                                .ifPresent(id -> MDC.put(TRACKING_ID_KEY, id));
                    })
                    .doOnSubscribe(s -> log.info("▶️ START {}", method))
                    .doOnComplete(() -> log.info("✅ END {} ({}ms)", method, System.currentTimeMillis() - start))
                    .doOnError(e -> log.error("💥 ERROR {} - {}", method, e.getMessage()))
                    .doFinally(st -> MDC.clear());
        }

        // Métodos no reactivos
        log.info("▶️ START {}", method);
        try {
            log.info("✅ END {} ({}ms)", method, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("💥 ERROR {} - {}", method, e.getMessage());
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
