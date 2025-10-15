package codes.alem.productservice.repository;

import codes.alem.productservice.entity.TipoProducto;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TipoProductoRepository extends ReactiveCrudRepository<TipoProducto, UUID> {
    Mono<TipoProducto> findById(Short id);
}
