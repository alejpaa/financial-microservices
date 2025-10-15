package codes.alem.clientservice.repository;

import codes.alem.clientservice.entity.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, UUID> {

    Mono<Cliente> findByCodigoUnico(String codigoUnico);
}
