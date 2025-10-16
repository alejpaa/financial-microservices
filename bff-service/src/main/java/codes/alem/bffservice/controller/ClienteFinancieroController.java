package codes.alem.bffservice.controller;

import codes.alem.bffservice.dto.ClienteConProductosDto;
import codes.alem.bffservice.service.ClienteFinancieroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bff")
@RequiredArgsConstructor
@Slf4j
public class ClienteFinancieroController {

    private final ClienteFinancieroService clienteFinancieroService;

    @PostMapping("/cliente")
    public Mono<ResponseEntity<ClienteConProductosDto>> obtenerClienteConProductos(
            @RequestBody String codigoUnicoEncriptado) {
        
        return clienteFinancieroService.obtenerClienteConProductos(codigoUnicoEncriptado)
                .map(ResponseEntity::ok)
                .onErrorReturn(IllegalArgumentException.class,
                    ResponseEntity.badRequest().build())
                .onErrorReturn(ResponseEntity.status(500).build());
    }
}
