package codes.alem.clientservice.controller;

import codes.alem.clientservice.dto.ClienteDto;
import codes.alem.clientservice.entity.Cliente;
import codes.alem.clientservice.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;


    @GetMapping("/{codigoUnico}")
    public Mono<ResponseEntity<ClienteDto>> getClienteByCodigoUnico(@PathVariable String codigoUnico) {
        return clienteService.findByCodigoUnico(codigoUnico)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
