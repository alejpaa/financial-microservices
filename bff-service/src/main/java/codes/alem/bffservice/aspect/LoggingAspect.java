package codes.alem.bffservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final Logger REQUEST_TRACKER = LoggerFactory.getLogger("REQUEST_TRACKER");
    private static final Logger EXTERNAL_CALLS = LoggerFactory.getLogger("EXTERNAL_CALLS");

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Pointcut("within(@org.springframework.stereotype.Service *) && !execution(* *..TrackingService.*(..))")
    public void businessServiceMethods() {}

    @Around("controllerMethods()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String endpoint = determineEndpoint(joinPoint);
        long startTime = System.currentTimeMillis();

        // Capturar el contexto MDC actual
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        REQUEST_TRACKER.info("🎯 REQUEST {} started", endpoint);

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Mono) {
                return ((Mono<?>) result)
                    .doOnEach(signal -> {
                        if (contextMap != null) {
                            MDC.setContextMap(contextMap);
                        }
                    })
                    .doOnSuccess(value -> {
                        long duration = System.currentTimeMillis() - startTime;
                        REQUEST_TRACKER.info("✅ REQUEST {} completed in {}ms", endpoint, duration);
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        REQUEST_TRACKER.error("❌ REQUEST {} failed in {}ms: {}", endpoint, duration, error.getMessage());
                    });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result)
                    .doOnEach(signal -> {
                        if (contextMap != null) {
                            MDC.setContextMap(contextMap);
                        }
                    })
                    .doOnComplete(() -> {
                        long duration = System.currentTimeMillis() - startTime;
                        REQUEST_TRACKER.info("✅ REQUEST {} completed in {}ms", endpoint, duration);
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        REQUEST_TRACKER.error("❌ REQUEST {} failed in {}ms: {}", endpoint, duration, error.getMessage());
                    });
            } else {
                long duration = System.currentTimeMillis() - startTime;
                REQUEST_TRACKER.info("✅ REQUEST {} completed in {}ms", endpoint, duration);
                return result;
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            REQUEST_TRACKER.error("❌ REQUEST {} failed in {}ms: {}", endpoint, duration, e.getMessage());
            throw e;
        }
    }

    @Around("businessServiceMethods()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Solo loggear servicios importantes, no métodos triviales
        if (isTrivalMethod(methodName)) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Mono) {
                return ((Mono<?>) result)
                    .doOnEach(signal -> {
                        if (contextMap != null) {
                            MDC.setContextMap(contextMap);
                        }
                    })
                    .doOnSuccess(value -> {
                        long duration = System.currentTimeMillis() - startTime;
                        if (duration > 50) {
                            log.debug("🔧 {}.{} completed in {}ms", className, methodName, duration);
                        }
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("🔧 {}.{} failed in {}ms: {}", className, methodName, duration, error.getMessage());
                    });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result)
                    .doOnEach(signal -> {
                        if (contextMap != null) {
                            MDC.setContextMap(contextMap);
                        }
                    })
                    .doOnComplete(() -> {
                        long duration = System.currentTimeMillis() - startTime;
                        if (duration > 50) {
                            log.debug("🔧 {}.{} completed in {}ms", className, methodName, duration);
                        }
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("🔧 {}.{} failed in {}ms: {}", className, methodName, duration, error.getMessage());
                    });
            } else {
                long duration = System.currentTimeMillis() - startTime;
                if (duration > 50) {
                    log.debug("🔧 {}.{} completed in {}ms", className, methodName, duration);
                }
                return result;
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("🔧 {}.{} failed in {}ms: {}", className, methodName, duration, e.getMessage());
            throw e;
        }
    }

    private String determineEndpoint(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        if (methodName.contains("obtenerClienteConProductos")) {
            return "POST /api/bff/cliente";
        } else if (methodName.contains("encrypt")) {
            return "POST /api/util/encrypt";
        }

        return className + "." + methodName;
    }

    private boolean isTrivalMethod(String methodName) {
        return methodName.equals("generateTrackingId") ||
               methodName.equals("generateCorrelationId") ||
               methodName.equals("encrypt") ||
               methodName.equals("decrypt");
    }
}
