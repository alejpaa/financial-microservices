package codes.alem.clientservice.service;

import codes.alem.clientservice.dto.ClienteDto;
import codes.alem.clientservice.entity.Cliente;
import reactor.core.publisher.Mono;


public interface ClienteService {

    Mono<ClienteDto> findByCodigoUnico(String codigoUnico);
}
