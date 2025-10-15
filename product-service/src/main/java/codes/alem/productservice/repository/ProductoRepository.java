package codes.alem.productservice.repository;


import codes.alem.productservice.entity.Producto;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface ProductoRepository extends ReactiveCrudRepository<Producto, UUID> {
    Flux<Producto> findByCodigoUnicoCliente(String codigoUnicoCliente);
}
