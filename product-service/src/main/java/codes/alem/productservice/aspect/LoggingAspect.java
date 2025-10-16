package codes.alem.productservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods() {}

    @Around("controllerMethods() || serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        log.info("Iniciando ejecución: {}.{}", className, methodName);
        log.debug("Parámetros: {}", Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Mono) {
                return ((Mono<?>) result)
                    .doOnSuccess(value -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("Completado exitosamente: {}.{} en {}ms", className, methodName, duration);
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("Error en ejecución: {}.{} después de {}ms - Error: {}",
                                className, methodName, duration, error.getMessage());
                    });
            } else if (result instanceof Flux) {
                return ((Flux<?>) result)
                    .doOnComplete(() -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("Completado exitosamente: {}.{} en {}ms", className, methodName, duration);
                    })
                    .doOnError(error -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.error("Error en ejecución: {}.{} después de {}ms - Error: {}",
                                className, methodName, duration, error.getMessage());
                    });
            } else {
                long duration = System.currentTimeMillis() - startTime;
                log.info("Completado exitosamente: {}.{} en {}ms", className, methodName, duration);
                return result;
            }
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error en ejecución: {}.{} después de {}ms - Error: {}",
                    className, methodName, duration, e.getMessage());
            throw e;
        }
    }

    @Before("controllerMethods()")
    public void logControllerAccess(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("Acceso a endpoint: {}.{}", className, methodName);
    }
}
