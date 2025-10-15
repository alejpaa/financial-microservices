package codes.alem.clientservice.service;

import codes.alem.clientservice.dto.ClienteDto;
import codes.alem.clientservice.entity.Cliente;
import reactor.core.publisher.Mono;


public interface ClienteService {

    public Mono<ClienteDto> findByCodigoUnico(String codigoUnico);
    public Mono<Cliente> findByCodigoUnico1(String codigoUnico);
}
