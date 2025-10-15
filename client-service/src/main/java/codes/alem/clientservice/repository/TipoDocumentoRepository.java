package codes.alem.clientservice.repository;

import codes.alem.clientservice.entity.TipoDocumento;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TipoDocumentoRepository extends ReactiveCrudRepository<TipoDocumento, Integer> {
    Mono<TipoDocumento> findById(Short id);
}
