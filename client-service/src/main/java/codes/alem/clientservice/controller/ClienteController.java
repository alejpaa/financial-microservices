package codes.alem.clientservice.controller;

import codes.alem.clientservice.dto.ClienteDto;
import codes.alem.clientservice.entity.Cliente;
import codes.alem.clientservice.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Tag(name = "Clientes", description = "API para gestión de información de clientes")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;


    @Operation(
        summary = "Obtener cliente por código único",
        description = "Busca un cliente específico utilizando su código único identificador. " +
                     "Retorna la información completa del cliente incluyendo nombres, apellidos, " +
                     "tipo de documento y número de documento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cliente encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClienteDto.class),
                examples = @ExampleObject(
                    name = "Cliente ejemplo",
                    value = """
                    {
                        "nombres": "Juan Carlos",
                        "apellidos": "Pérez García",
                        "tipoDocumento": "DNI",
                        "numeroDocumento": "12345678"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado con el código único proporcionado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error 404",
                    value = """
                    {
                        "error": "Cliente no encontrado",
                        "message": "No existe un cliente con el código único: CLI999"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error interno",
                    value = """
                    {
                        "error": "Internal Server Error",
                        "message": "Error al procesar la solicitud"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{codigoUnico}")
    public Mono<ResponseEntity<ClienteDto>> getClienteByCodigoUnico(
        @Parameter(
            description = "Código único del cliente que se desea consultar",
            example = "CLI001",
            required = true
        )
        @PathVariable String codigoUnico) {
        return clienteService.findByCodigoUnico(codigoUnico)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
