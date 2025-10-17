package codes.alem.bffservice.controller;

import codes.alem.bffservice.dto.ClienteConProductosDto;
import codes.alem.bffservice.service.ClienteFinancieroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(
    name = "BFF - Cliente Financiero",
    description = "Backend for Frontend (BFF) que integra información de clientes y productos financieros. " +
                 "Proporciona una vista consolidada de la información del cliente junto con todos sus productos."
)
@RestController
@RequestMapping("/api/bff")
@RequiredArgsConstructor
@Slf4j
public class ClienteFinancieroController {

    private final ClienteFinancieroService clienteFinancieroService;

    @Operation(
        summary = "Obtener información completa del cliente con sus productos financieros",
        description = "Endpoint principal del BFF que consolida información del cliente y sus productos financieros. " +
                     "Recibe un código único encriptado, lo desencripta y consulta tanto el microservicio de clientes " +
                     "como el de productos para devolver una respuesta unificada. " +
                     "Incluye tracking de solicitudes para trazabilidad completa.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Información del cliente y productos obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ClienteConProductosDto.class),
                examples = @ExampleObject(
                    name = "Respuesta completa",
                    value = """
                    {
                        "nombres": "Juan Carlos",
                        "apellidos": "Pérez García",
                        "tipoDocumento": "DNI",
                        "numeroDocumento": "12345678",
                        "productosFinancieros": [
                            {
                                "tipoProducto": "Cuenta de Ahorro",
                                "nombreProducto": "Ahorro Premium",
                                "numeroProducto": "4001234567890123",
                                "saldo": 5000.50
                            },
                            {
                                "tipoProducto": "Tarjeta de Crédito",
                                "nombreProducto": "Visa Gold",
                                "numeroProducto": "4002987654321098",
                                "saldo": 2500.75
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Código encriptado inválido o no se puede desencriptar",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de validación",
                    value = """
                    {
                        "error": "Bad Request",
                        "message": "El código encriptado no es válido o no se puede desencriptar",
                        "trackingId": "BFF-abc123"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autorizado - Token de autenticación requerido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error de autenticación",
                    value = """
                    {
                        "error": "Unauthorized",
                        "message": "Token de autenticación requerido"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cliente no encontrado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Cliente no encontrado",
                    value = """
                    {
                        "error": "Not Found",
                        "message": "No se encontró el cliente con el código proporcionado",
                        "trackingId": "BFF-abc123"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor o falla en microservicios",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error interno",
                    value = """
                    {
                        "error": "Internal Server Error",
                        "message": "Error al procesar la solicitud. Verifique el estado de los microservicios",
                        "trackingId": "BFF-abc123"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "503",
            description = "Microservicios no disponibles",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Servicio no disponible",
                    value = """
                    {
                        "error": "Service Unavailable",
                        "message": "Uno o más microservicios no están disponibles",
                        "trackingId": "BFF-abc123"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/cliente")
    public Mono<ResponseEntity<ClienteConProductosDto>> obtenerClienteConProductos(
            @Parameter(
                description = "Código único del cliente encriptado. Este código debe estar encriptado " +
                             "usando el algoritmo de encriptación configurado en el sistema. " +
                             "El código desencriptado debe corresponder a un cliente válido.",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                required = true,
                schema = @Schema(type = "string", format = "encrypted")
            )
            @RequestBody String codigoUnicoEncriptado) {
        
        return clienteFinancieroService.obtenerClienteConProductos(codigoUnicoEncriptado)
                .map(ResponseEntity::ok)
                .onErrorReturn(IllegalArgumentException.class,
                    ResponseEntity.badRequest().build())
                .onErrorReturn(ResponseEntity.status(500).build());
    }
}
